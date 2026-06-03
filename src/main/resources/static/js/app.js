var ZADERZHKA_POISKA_MS = 300;

var KOLICHESTVO_ZVEZD = 5;

function initOfferSearch() {
    var poleZaprosa = document.getElementById('searchInput');
    var filtrKategoriy = document.getElementById('categoryFilter');
    var filtrUrovney = document.getElementById('levelFilter');

    if (!poleZaprosa) return;

    var taymerDebauns;

    function vypolnitPoiskObyavleniy() {
        var stroka = poleZaprosa.value.trim();
        var kategoriya = filtrKategoriy.value;
        var uroven = filtrUrovney.value;

        var parametry = sobratParametryPoiska(stroka, kategoriya, uroven);

        fetch('/api/offers?' + parametry.toString(), {
            headers: { 'Accept': 'application/json', 'X-Requested-With': 'XMLHttpRequest' }
        })
        .then(function(otvet) { return otvet.json(); })
        .then(function(dannye) {
            otrisovatRezultatPoiska(dannye.content || []);
        })
        .catch(function(oshibka) {
            console.error('Не удалось получить объявления:', oshibka);
        });
    }

    poleZaprosa.addEventListener('input', function() {
        clearTimeout(taymerDebauns);
        taymerDebauns = setTimeout(vypolnitPoiskObyavleniy, ZADERZHKA_POISKA_MS);
    });

    filtrKategoriy.addEventListener('change', vypolnitPoiskObyavleniy);
    filtrUrovney.addEventListener('change', vypolnitPoiskObyavleniy);
}

function sobratParametryPoiska(stroka, kategoriya, uroven) {
    var parametry = new URLSearchParams();
    if (stroka) parametry.set('keyword', stroka);
    if (kategoriya) parametry.set('category', kategoriya);
    if (uroven) parametry.set('level', uroven);
    return parametry;
}

function otrisovatRezultatPoiska(massivObyavleniy) {
    var setka = document.getElementById('offersGrid');
    var pustoyBlok = document.getElementById('emptyState');

    if (massivObyavleniy.length === 0) {
        setka.innerHTML = '';
        if (pustoyBlok) {
            pustoyBlok.style.display = 'block';
            pustoyBlok.innerHTML = '<p>По вашему запросу ничего не найдено.</p>';
        }
        return;
    }

    if (pustoyBlok) pustoyBlok.style.display = 'none';

    var html = '';
    for (var i = 0; i < massivObyavleniy.length; i++) {
        html += sobratKartochkuObyavleniya(massivObyavleniy[i]);
    }
    setka.innerHTML = html;
}

function sobratKartochkuObyavleniya(obyavlenie) {
    var klassKategorii = 'badge-' + (obyavlenie.skillCategory || '').toLowerCase();
    var blokTseny = sobratBlokTseny(obyavlenie);
    var zvezdy = sobratZvezdyReytinga(obyavlenie.ownerRating || 0);
    var korotkoeOpisanie = obrezatOpisanie(obyavlenie.description, 120);

    return '<article class="kartochka-obyavleniya">' +
        '<div class="kartochka-obyavleniya-shapka">' +
        '<span class="metka ' + klassKategorii + '">' + (obyavlenie.skillCategory || '') + '</span>' +
        '<span class="metka badge-level">' + (obyavlenie.skillLevel || '') + '</span>' +
        '</div>' +
        '<h3 class="kartochka-obyavleniya-zagolovok"><a href="/offers/' + obyavlenie.id + '">' + ekranirovatHtml(obyavlenie.title) + '</a></h3>' +
        '<p class="kartochka-obyavleniya-opisanie">' + ekranirovatHtml(korotkoeOpisanie) + '</p>' +
        '<div class="kartochka-obyavleniya-meta">' +
        '<span><a href="/profile/' + obyavlenie.ownerUsername + '">' + ekranirovatHtml(obyavlenie.ownerUsername) + '</a></span>' +
        '<span class="rating-stars">' + zvezdy + '</span>' +
        '</div>' +
        '<div class="kartochka-obyavleniya-podval">' + blokTseny +
        '<span class="offer-meta">' + (obyavlenie.hoursPerSession || 1) + ' ч / занятие</span>' +
        '</div></article>';
}

function sobratBlokTseny(obyavlenie) {
    if (obyavlenie.hourlyRate) {
        return '<span class="offer-price">' + obyavlenie.hourlyRate + ' ' + (obyavlenie.rateCurrency || 'EUR') + '/час</span>';
    }
    return '<span class="offer-price free">Бартер без оплаты</span>';
}

function sobratZvezdyReytinga(reyting) {
    var zapolnennyh = Math.round(reyting);
    var rezultat = '';
    for (var nomer = 1; nomer <= KOLICHESTVO_ZVEZD; nomer++) {
        if (nomer <= zapolnennyh) {
            rezultat += '<span class="star filled">&#9733;</span>';
        } else {
            rezultat += '<span class="star empty">&#9734;</span>';
        }
    }
    return rezultat;
}

function obrezatOpisanie(tekst, maksimum) {
    var stroka = tekst || '';
    if (stroka.length > maksimum) {
        return stroka.substring(0, maksimum - 3) + '...';
    }
    return stroka;
}

function ekranirovatHtml(tekst) {
    if (!tekst) return '';
    var divDlyaEkranirovaniya = document.createElement('div');
    divDlyaEkranirovaniya.appendChild(document.createTextNode(tekst));
    return divDlyaEkranirovaniya.innerHTML;
}
