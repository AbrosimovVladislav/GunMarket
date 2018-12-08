package com.gunmarket.repository;

import com.gunmarket.web.HttpParameter;

import java.util.List;
import java.util.Map;

import static com.gunmarket.web.HttpParameter.COMPLEX_PARAM_TYPE;
import static com.gunmarket.web.HttpParameter.OBJECTSIMPLE_PARAM_TYPE;

public class QueryBuilder {

    private static final String EQUALLY_KEYWORD = " = ";
    private static final String OR_KEYWORD = " OR ";
    private static final String SELECT_KEYWORD = "SELECT ";
    private static final String FROM_KEYWORD = "FROM ";
    private static final String WHERE_KEYWORD = " WHERE ";
    private static final String IN_KEYWORD = " IN ";
    private static final String AND_KEYWORD = " AND ";
    private static final String AS_KEYWORD = " AS ";
    private static final String PARAMETER_STARTING = ":p";
    private static final String PARAMETER_ENDING = "n";
    private static final String ID_LOWER_PARAMETER_ADDITION = "_id";
    private static final String ID_UPPER_PARAMETER_ADDITION = "_Id";
    private static final String PLURAL_ENDING_S = "s";
    private static final String COMMA = ".";
    private static final String SPACE = " ";
    private static final String CLOSING_BRACKET_REGEX = "\\)";
    private static final String CLOSING_BRACKET = ")";
    private static final String OPENING_BRACKET = "(";

    private static int paramBuilderCounter = 0;

    public String build(String entityName, Map<HttpParameter, List<String>> params) {

        String resultHqlQuery = "";
        String connectorLine = createConnectorLine(entityName);

        for (Map.Entry<HttpParameter, List<String>> paramEntry : params.entrySet()) {
            String paramName = paramEntry.getKey().getParamName();
            String paramType = paramEntry.getKey().getParamType();
            List<String> paramValues = paramEntry.getValue();

            if (paramType.equals(COMPLEX_PARAM_TYPE)) {
                resultHqlQuery = getComplexParamQueryPart(entityName, paramName, paramValues, resultHqlQuery, connectorLine);
            } else {
                resultHqlQuery = getSimpleParamQueryPart(entityName, paramName, paramValues, resultHqlQuery, connectorLine, paramType);
            }
        }

        //ToDo Удалить вывод
        System.out.println("Вывод текущей части " + resultHqlQuery);

        resultHqlQuery = resultHqlQuery.replaceFirst(CLOSING_BRACKET_REGEX, "");
        //ToDo Удалить вывод
        System.out.println("Вывод результата без закр.скобки " + resultHqlQuery);
        return resultHqlQuery.substring(0, resultHqlQuery.length() - connectorLine.length() + 1);

    }

    //Построение простой части
    private String getSimpleParamQueryPart(String entityName, String paramName, List<String> paramValues, String resultHqlQuery, String connectorLine, String paramType) {
        if (paramType.equals(OBJECTSIMPLE_PARAM_TYPE)) {
            paramName = paramName + ID_LOWER_PARAMETER_ADDITION;
        }
        return createCleaningPartOfQuery(resultHqlQuery,
                createInitialPartOfSimpleParamQuery(entityName)
                        .append(createParamFillingPartOfSimpleParamQuery(paramName, paramValues)),
                connectorLine);
    }

    private StringBuilder createInitialPartOfSimpleParamQuery(String entityName) {
        return new StringBuilder(FROM_KEYWORD)
                .append(entityName)
                .append(WHERE_KEYWORD);
    }

    private String createParamFillingPartOfSimpleParamQuery(String paramName, List<String> paramValues) {
        StringBuilder currentQPArt = new StringBuilder();
        for (String paramValue : paramValues) {
            currentQPArt
                    .append(paramName)
                    .append(EQUALLY_KEYWORD)
                    .append(PARAMETER_STARTING)
                    .append(paramValue)
                    .append(paramBuilderCounter)
                    .append(PARAMETER_ENDING)
                    .append(OR_KEYWORD);
            paramBuilderCounter++;
        }
        return currentQPArt.toString();
    }

    //Построение составной части
    private String getComplexParamQueryPart(String entityName, String paramName, List<String> paramValues, String resultHqlQuery, String connectorLine) {
        return createCleaningPartOfQuery(resultHqlQuery,
                createInitialPartOfComplexParamQuery(entityName, paramName)
                        .append(createParamFillingPartOfComplexParamQuery(paramName, paramValues)),
                connectorLine);
    }

    private StringBuilder createInitialPartOfComplexParamQuery(String entityName, String paramName) {
        return new StringBuilder(SELECT_KEYWORD)
                .append(entityName.toLowerCase())
                .append(PLURAL_ENDING_S)
                .append(SPACE)
                .append(FROM_KEYWORD)
                .append(firstUpperCase(replaceLastChar(paramName)))
                .append(AS_KEYWORD)
                .append(firstUpperCase(replaceLastChar(paramName)))
                .append(WHERE_KEYWORD); //SELECT products FROM Shop AS Shop WHERE
    }

    private String createParamFillingPartOfComplexParamQuery(String paramName, List<String> paramValues) {
        StringBuilder currentQPArt = new StringBuilder();
        for (String paramValue : paramValues) {
            currentQPArt
                    .append(firstUpperCase(replaceLastChar(paramName)))
                    .append(COMMA)
                    .append(replaceLastChar(paramName))
                    .append(ID_UPPER_PARAMETER_ADDITION)
                    .append(EQUALLY_KEYWORD)
                    .append(PARAMETER_STARTING)
                    .append(paramValue)
                    .append(paramBuilderCounter)
                    .append(PARAMETER_ENDING)
                    .append(OR_KEYWORD);
            paramBuilderCounter++;
        }
        return currentQPArt.toString();
    }

    //Общие методы для все параметров
    private String createCleaningPartOfQuery(String resultQuery, StringBuilder currentQPArt, String connectorLine) {
        return resultQuery +
                currentQPArt.delete(currentQPArt.length() - 4, currentQPArt.length() - 1).toString() +
                connectorLine;
    }

    private String createConnectorLine(String entityName) {
        return new StringBuilder(CLOSING_BRACKET)
                .append(AND_KEYWORD)
                .append(entityName.toLowerCase())
                .append(ID_UPPER_PARAMETER_ADDITION)
                .append(IN_KEYWORD)
                .append(OPENING_BRACKET)
                .toString();
    }

    //Статические методы
    private static String replaceLastChar(String str) {
        if (str != null && str.length() > 0) {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    private static String firstUpperCase(String word) {
        if (word == null || word.isEmpty()) return "";//или return word;
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }

}