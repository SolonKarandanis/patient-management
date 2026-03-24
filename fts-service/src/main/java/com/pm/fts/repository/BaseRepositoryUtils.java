package com.pm.fts.repository;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.json.JsonData;
import com.pm.fts.exception.UnsupportedEsQueryException;
import com.pm.fts.utils.ArrayUtils;
import com.pm.fts.web.dto.Paging;
import com.pm.fts.web.dto.SearchCriterion;
import lombok.extern.java.Log;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

@Log
public class BaseRepositoryUtils {

    protected BaseRepositoryUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * if Paging p is not set, return page 0 and limit 10
     *
     * @return Pageable
     */
    public static Pageable toPageable(Paging paging) {
        // if P null, return 0, 10
        if (!(paging == null)) {
            int page = Math.max(paging.getPage(), 0);
            int limit = paging.getLimit() > 0 ? paging.getLimit() : 10;
            String sortDir = paging.getSortDirection();
            log.info("--------> page: " + page);
            log.info("--------> limit: " + limit);
            List<String> sortFields = paging.getSortFields() != null && !paging.getSortFields().isEmpty()
                    ? paging.getSortFields()
                    : null;
            return handlePaging(sortFields, page, limit, sortDir);
        } else {
            return PageRequest.of(0, 10);
        }
    }

    private static Pageable handlePaging(List<String> sortFields, int page, int limit, String sortDir) {
        if (sortFields != null) {
            Direction direction = Direction.DESC;
            try {
                direction = Direction.fromString(sortDir);
            } catch (Exception e) {
                log.log(Level.WARNING, "Invalid Direction Received");
            }
            final Direction dir = direction;
            List<Order> orders = sortFields.stream().map(it -> new Order(dir, it).ignoreCase()).toList();
            return PageRequest.of(page, limit, Sort.by(orders));
        } else {
            return PageRequest.of(page, limit);
        }
    }

    public static FetchSourceFilter getSourceFilter(List<String> resultFields, List<String> excludeFields) {
        String[] defaultExcludeFields = { "userGroupsThatHaveAccess", "orgsThatHaveAccess" };
        String[] includeFields = null;
        String[] allExcludedFields = null;
        if (!CollectionUtils.isEmpty(excludeFields)) {
            for (String field : excludeFields) {
                allExcludedFields = ObjectUtils.addObjectToArray(defaultExcludeFields, field);
            }
        } else {
            allExcludedFields = defaultExcludeFields;
        }
        if (!CollectionUtils.isEmpty(resultFields)) {
            includeFields = ArrayUtils.toStringArray(resultFields);
        }
        assert includeFields != null;
        assert allExcludedFields != null;
        return new FetchSourceFilter(false,includeFields, allExcludedFields);
    }

    public static QueryVariant getCriterionQuery(SearchCriterion criterion) throws UnsupportedEsQueryException {
        if (criterion.getIsNotNull() != null) {
            return getExistsCriterionQuery(criterion).build();
        } else if (SearchCriterion.SearchType.WILDCARD.equals(criterion.getSearchType())) {
            return getWildCardCriterionQuery(criterion).build();
        } else if (SearchCriterion.SearchType.TERM.equals(criterion.getSearchType())) {
            return getTermCriterionQuery(criterion).build();
        } else if (SearchCriterion.SearchType.RANGE_INCLUSIVE.equals(criterion.getSearchType())
                || SearchCriterion.SearchType.RANGE_EXCLUSIVE.equals(criterion.getSearchType())) {
            return getRangeCriterionQuery(criterion).build();
        } else if (SearchCriterion.SearchType.COMPLEX.equals(criterion.getSearchType())) {
            return getComplexCriterionQuery(criterion).build();
        } else if (SearchCriterion.SearchType.MATCH.equals(criterion.getSearchType())) {
            return getMatchCriterionQuery(criterion).build();
        } else if (SearchCriterion.SearchType.MATCH_PREFIX.equals(criterion.getSearchType())) {
            return getMatchPrefixCriterionQuery(criterion).build();
        } else if (SearchCriterion.SearchType.MATCH_PREFIX_OR_WILDCARD.equals(criterion.getSearchType())) {
            return getMatchPrefixOrWildCardCriterionQuery(criterion).build();
        } else if (SearchCriterion.SearchType.NESTED.equals(criterion.getSearchType())) {
            return getNestedCriterionQuery(criterion).build();
        } else {
            throw new UnsupportedEsQueryException("search.type.unsupported");
        }
    }

    private static ExistsQuery.Builder getExistsCriterionQuery(SearchCriterion criterion) {
        ExistsQuery.Builder existsQueryBuilder = QueryBuilders.exists();
        existsQueryBuilder.field(criterion.getField());
        return existsQueryBuilder;
    }

    private static BoolQuery.Builder getMatchCriterionQuery(SearchCriterion criterion)
            throws UnsupportedEsQueryException {
        BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();
        for (Object criterionValue : criterion.getValues()) {
            final String wQuery = String.valueOf(getCriterionValue(criterion, criterionValue));
            MatchQuery.Builder matchQueryBuilder = QueryBuilders.match();
            matchQueryBuilder.field(criterion.getField()).query(wQuery).minimumShouldMatch("100%");
            boolQueryBuilder.should(new Query(matchQueryBuilder.build()));
        }
        if (!boolQueryBuilder.hasClauses()) {
            throw new UnsupportedEsQueryException("criterion.value.missing");
        }
        return boolQueryBuilder;
    }

    private static MatchPhrasePrefixQuery.Builder getMatchPrefixCriterionQuery(SearchCriterion criterion) throws UnsupportedEsQueryException {
        Object value = CollectionUtils.firstElement(criterion.getValues());
        if (value == null) {
            throw new UnsupportedEsQueryException("criterion.value.missing");
        }
        final String wQuery = String.valueOf(getCriterionValue(criterion, value));
        MatchPhrasePrefixQuery.Builder matchQueryBuilder = QueryBuilders.matchPhrasePrefix();
        matchQueryBuilder.field(criterion.getField()).query(wQuery);
        return matchQueryBuilder;
    }

    private static BoolQuery.Builder getMatchPrefixOrWildCardCriterionQuery(SearchCriterion criterion) throws UnsupportedEsQueryException {
        Object value = CollectionUtils.firstElement(criterion.getValues());
        if (value == null) {
            throw new UnsupportedEsQueryException("criterion.value.missing");
        }
        final String wQuery = String.valueOf(getCriterionValue(criterion, value));
        BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();
        MatchPhrasePrefixQuery.Builder matchQueryBuilder = QueryBuilders.matchPhrasePrefix();
        matchQueryBuilder.field(criterion.getField()).query(wQuery);
        boolQueryBuilder.should(new Query(matchQueryBuilder.build()));

        WildcardQuery.Builder wildCardBuilder = QueryBuilders.wildcard();
        String fieldKeyword = criterion.getField() + ".keyword";
        wildCardBuilder.field(fieldKeyword).value("*" + wQuery.toLowerCase() + "*");
        boolQueryBuilder.should(new Query(wildCardBuilder.build()));
        return boolQueryBuilder;
    }

    private static BoolQuery.Builder getWildCardCriterionQuery(SearchCriterion criterion)
            throws UnsupportedEsQueryException {
        BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();
        for (Object criterionValue : criterion.getValues()) {
            String stringValue = getCriterionValue(criterion, criterionValue).toString();
            WildcardQuery.Builder wildCardBuilder = QueryBuilders.wildcard();
            wildCardBuilder.field(criterion.getField()).value("*" + stringValue.toLowerCase() + "*");
            boolQueryBuilder.should(new Query(wildCardBuilder.build()));
        }
        return boolQueryBuilder;
    }

    private static TermsQuery.Builder getTermCriterionQuery(SearchCriterion criterion)
            throws UnsupportedEsQueryException {
        TermsQuery.Builder termsQueryBuilder = QueryBuilders.terms();
        List<FieldValue> fieldValues = getListCriterionValues(criterion).stream().map(FieldValue::of).toList();
        TermsQueryField values = new TermsQueryField.Builder().value(fieldValues).build();
        termsQueryBuilder.field(criterion.getField()).terms(values);
        return termsQueryBuilder;
    }

    private static BoolQuery.Builder getComplexCriterionQuery(SearchCriterion criterion) throws UnsupportedEsQueryException {
        BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();
        for (Object criterionValue : criterion.getValues()) {
            final String wQuery = getCriterionValue(criterion, criterionValue).toString();
            WildcardQuery.Builder wildCardBuilder = QueryBuilders.wildcard();
            wildCardBuilder.field(criterion.getKeywordField()).value(wQuery);
            boolQueryBuilder.should(new Query(wildCardBuilder.build()));
        }
        return boolQueryBuilder;
    }

    private static List<String> getListCriterionValues(SearchCriterion criterion) throws UnsupportedEsQueryException {
        List<String> result = new ArrayList<>();
        for (Object value : criterion.getValues()) {
            result.add(getCriterionValue(criterion, value).toString());
        }
        return result;
    }

    private static RangeQuery.Builder getRangeCriterionQuery(SearchCriterion criterion)
            throws UnsupportedEsQueryException {
        RangeQuery.Builder rangeQueryBuilder = QueryBuilders.range();
        if (criterion.getFormat() != null && !"".equalsIgnoreCase(criterion.getFormat().trim()) && SearchCriterion.Type.DATE.equals(criterion.getType())) {
            try {
                rangeQueryBuilder.date(date -> date.field(criterion.getField()).format(criterion.getFormat()));
            } catch (IllegalArgumentException e) {
                throw new UnsupportedEsQueryException("criterion.format.unsupported");
            }
        }
        if (criterion.getFrom() != null && !"".contentEquals(criterion.getFrom())) {
            JsonData value = JsonData.of(getCriterionValue(criterion, criterion.getFrom()));
            if (SearchCriterion.SearchType.RANGE_INCLUSIVE.equals(criterion.getSearchType())) {
                rangeQueryBuilder.untyped(type -> type.field(criterion.getField()).gte(value));
            } else {
                rangeQueryBuilder.untyped(type -> type.field(criterion.getField()).gt(value));
            }
        }
        if (criterion.getTo() != null && !"".contentEquals(criterion.getTo())) {
            JsonData value = JsonData.of(getCriterionValue(criterion, criterion.getTo()));
            if (SearchCriterion.SearchType.RANGE_INCLUSIVE.equals(criterion.getSearchType())) {
                rangeQueryBuilder.untyped(type -> type.field(criterion.getField()).lte(value));
            } else {
                rangeQueryBuilder.untyped(type -> type.field(criterion.getField()).lt(value));
            }
        }
        return rangeQueryBuilder;
    }

    private static NestedQuery.Builder getNestedCriterionQuery(SearchCriterion criterion) throws UnsupportedEsQueryException {
        BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();
        for (Object value : criterion.getValues()) {
            String valueStr = String.valueOf(value);
            if (!StringUtils.hasLength(valueStr)) {
                throw new UnsupportedEsQueryException("criterion.value.missing");
            }
            log.info("--------> getNestedCriterionQuery -> PiaValue: " + value);
            final String wQuery = getCriterionValue(criterion, value.toString()).toString();

//            setBoolQuery(boolQueryBuilder, criterion, wQuery);
        }
        NestedQuery.Builder nested = QueryBuilders.nested();
        nested.scoreMode(ChildScoreMode.None).query(new Query(boolQueryBuilder.build()));
        return nested;
    }

//    private static void setBoolQuery(BoolQuery.Builder boolQB, SearchCriterion criterion, String wQuery) {
//        if (SearchCriterion.Path.PIAS.equals(criterion.getPath())) {
//            int criterionValueLength = wQuery.length();
//            //if search field is pia code
//            if (Objects.equals(criterion.getField(), "code")) {
//                //1st & 2nd lvl pia
//                if (criterionValueLength == 1 || criterionValueLength == 4) {
//                    final String query = "*" + wQuery + "*";
//                    WildcardQuery.Builder wildCardBuilder = QueryBuilders.wildcard();
//                    String pathField = "pias.absolutePath";
//                    wildCardBuilder.field(pathField).value(wQuery);
//                    boolQB.should(new Query(wildCardBuilder.build()));
//                }
//                //3rd lvl pia
//                if (criterionValueLength == 7) {
//                    MatchQuery.Builder matchQueryBuilder = QueryBuilders.match();
//                    String pathField = "pias." + criterion.getField();
//                    matchQueryBuilder.field(pathField).query(wQuery);
//                    boolQB.must(new Query(matchQueryBuilder.build()));
//                }
//            }
//
//        }
//    }

    /**
     * Note: NO CHECK FOR INDEX TO EXIST
     *
     * @param criterion the SearchCriterion
     * @param criterionValue the supplied value
     * @return Object
     * @throws UnsupportedEsQueryException
     */
    static Object getCriterionValue(SearchCriterion criterion, Object criterionValue)
            throws UnsupportedEsQueryException {
        Object result = null;
        try {
            if (SearchCriterion.Type.TEXT.equals(criterion.getType())) {
                result = ((String) criterionValue).trim();
            } else if (SearchCriterion.Type.DATE.equals(criterion.getType())) {
                //verify it is parsable
                new SimpleDateFormat(criterion.getFormat()).parse((String) criterionValue);
                result = criterionValue;
            } else if (SearchCriterion.Type.INTEGER.equals(criterion.getType())) {
                result = criterionValue;
            } else if (SearchCriterion.Type.DOUBLE.equals(criterion.getType())) {
                result = criterionValue;
            } else if (SearchCriterion.Type.BOOLEAN.equals(criterion.getType())) {
                result = criterionValue;
            }
            else {
                throw new UnsupportedEsQueryException("search.criterion.type.unsupported");
            }
        } catch (NullPointerException e) {
            throw new UnsupportedEsQueryException("criterion.null", e);
        } catch (UnsupportedEsQueryException e) {
            throw e;
        } catch (Exception e) {
            throw new UnsupportedEsQueryException("criterion.value.unparsable", e);
        }
        return result;
    }

}
