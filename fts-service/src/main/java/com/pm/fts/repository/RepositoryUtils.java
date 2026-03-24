package com.pm.fts.repository;

import co.elastic.clients.elasticsearch._types.query_dsl.*;
import com.pm.fts.exception.BadRequestException;
import com.pm.fts.web.dto.DocumentSearchRequest;

import java.util.Objects;

public class RepositoryUtils extends BaseRepositoryUtils{
    private RepositoryUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static BoolQuery.Builder getRoleAccessQuery(DocumentSearchRequest payload) {
        BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();
        TermsQuery.Builder termsQueryBuilder = QueryBuilders.terms();
//        if (Objects.equals(DocumentSearchRequest.Role.SA, payload.getRole())) {
//            boolQueryBuilder.must(new Query(QueryBuilders.matchAll().build()));
//        }
//
//        if (Objects.equals(DocumentSearchRequest.Role.REQUISTIONER, payload.getRole())) {
//            if (payload.getUsergroupIds() == null) {
//                throw new BadRequestException("search.userGroupId.missing");
//            }
//            TermsQueryField values = new TermsQueryField.Builder().value(payload.getUsergroupIds().stream().map(FieldValue::of).toList()).build();
//            termsQueryBuilder.field("userGroupsThatHaveAccess").terms(values);
//            boolQueryBuilder.must(new Query(termsQueryBuilder.build()));
//
//        }
//
//        if (Objects.equals(DocumentSearchRequest.Role.ADMIN, payload.getRole())
//                || Objects.equals(DocumentSearchRequest.Role.BUYER, payload.getRole())
//                || Objects.equals(DocumentSearchRequest.Role.EO, payload.getRole())) {
//            if (payload.getOrgId() == null) {
//                throw new BadRequestException("search.orgId.missing");
//            }
//            TermsQueryField values = new TermsQueryField.Builder().value(List.of(payload.getOrgId()).stream().map(FieldValue::of).toList()).build();
//            termsQueryBuilder.field("orgsThatHaveAccess").terms(values);
//            boolQueryBuilder.must(new Query(termsQueryBuilder.build()));
//        }
        return boolQueryBuilder;
    }
}
