package com.skillswap.dialect;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * Атрибут-процессор {@code ss:rating-stars="${value}"}. Принимает число 0..5
 * и заменяет элемент готовым span-ом с пятью звёздочками — заполненными и пустыми.
 * Достаточно подключить его в шаблонах вместо ручной верстки звёзд.
 */
public class RatingStarsProcessor extends AbstractAttributeTagProcessor {

    private static final String ATTR_NAME = "rating-stars";
    private static final int PRECEDENCE = 10000;

    public RatingStarsProcessor(String dialectPrefix) {
        super(TemplateMode.HTML, dialectPrefix, null, false, ATTR_NAME, true, PRECEDENCE, true);
    }

    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag,
                             AttributeName attributeName, String attributeValue,
                             IElementTagStructureHandler handler) {
        IStandardExpressionParser parser = StandardExpressions.getExpressionParser(context.getConfiguration());
        IStandardExpression expression = parser.parseExpression(context, attributeValue);
        Object result = expression.execute(context);

        double rating = 0;
        if (result instanceof Number) {
            rating = ((Number) result).doubleValue();
        }

        StringBuilder sb = new StringBuilder("<span class=\"rating-stars\" title=\"");
        sb.append(String.format("%.1f", rating)).append(" / 5\">");
        int fullStars = (int) Math.round(rating);
        for (int i = 1; i <= 5; i++) {
            if (i <= fullStars) {
                sb.append("<span class=\"star filled\">&#9733;</span>");
            } else {
                sb.append("<span class=\"star empty\">&#9734;</span>");
            }
        }
        sb.append("</span>");

        handler.replaceWith(sb.toString(), false);
    }
}
