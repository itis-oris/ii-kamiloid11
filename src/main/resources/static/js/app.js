// SkillSwap — клиентский скрипт каталога объявлений.
// Здесь живёт живой поиск, обновление сетки карточек и вспомогательные утилиты.
// Намеренно стараюсь не тащить сюда фреймворки — ванила покрывает всё нужное.

// Сколько миллисекунд ждать после последнего нажатия клавиши перед запросом на сервер.
// TODO вынести в конфиг потом
var ZADERZHKA_POISKA_MS = 300;

var KOLICHESTVO_ZVEZD = 5;

// Точка входа: вешает на форму фильтров живой поиск через AJAX.
function initOfferSearch() {
    var poleZaprosa = document.getElementById('searchInput');
    var filtrKategoriy = document.getElementById('categoryFilter');
    var filtrUrovney = document.getElementById('levelFilter');

    // Если на странице нет блока поиска — просто выходим.
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
        // даём пользователю допечатать перед тем как дёрнуть бэк
        taymerDebauns = setTimeout(vypolnitPoiskObyavleniy, ZADERZHKA_POISKA_MS);
    });

    filtrKategoriy.addEventListener('change', vypolnitPoiskObyavleniy);
    filtrUrovney.addEventListener('change', vypolnitPoiskObyavleniy);
}

// Собирает URLSearchParams из непустых значений фильтров.
function sobratParametryPoiska(stroka, kategoriya, uroven) {
    var parametry = new URLSearchParams();
    if (stroka) parametry.set('keyword', stroka);
    if (kategoriya) parametry.set('category', kategoriya);
    if (uroven) parametry.set('level', uroven);
    return parametry;
}

// Перерисовывает сетку карточек по итогам ответа сервера.
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

    // Использую обычный цикл, а не map+join — так удобнее ставить точку останова при отладке.
    var html = '';
    for (var i = 0; i < massivObyavleniy.length; i++) {
        html += sobratKartochkuObyavleniya(massivObyavleniy[i]);
    }
    setka.innerHTML = html;
}

// Возвращает HTML-разметку одной карточки. Большую функцию специально не выношу в шаблон —
// шаблон Thymeleaf-фрагмента работает только при server-side рендере, а тут чистый клиент.
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

// Простейший XSS-щит для текста, который мы вставляем через innerHTML.
function ekranirovatHtml(tekst) {
    if (!tekst) return '';
    var divDlyaEkranirovaniya = document.createElement('div');
    divDlyaEkranirovaniya.appendChild(document.createTextNode(tekst));
    return divDlyaEkranirovaniya.innerHTML;
}
