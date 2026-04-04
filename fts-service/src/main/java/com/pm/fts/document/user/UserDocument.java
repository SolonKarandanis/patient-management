package com.pm.fts.document.user;

import com.pm.fts.document.AbstractDocument;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.elasticsearch.annotations.*;

import java.util.List;

@SuperBuilder
@Document(indexName = "#{@indexPrefix}auth_service_users", createIndex = true)
@Getter
@Setter
@Setting(settingPath = "elasticsearch-settings.json")
public class UserDocument extends AbstractDocument {

    public UserDocument(AbstractDocument abs) {
        super(abs);
    }

    public UserDocument() {
        super();
    }

    @Field(type = FieldType.Keyword, name = "username")
    protected String username;

    @MultiField(mainField = @Field(index = true, type = FieldType.Text, name = "first_name", analyzer = "greek_lowercase_analyzer"),
            otherFields = {
                    @InnerField(index = true, type = FieldType.Keyword, suffix = "keyword",  normalizer = "lowercase_normalizer"),
                    @InnerField(index = true, type = FieldType.Text, suffix = "ngram", analyzer = "ngram_analyzer", searchAnalyzer = "greek_lowercase_analyzer")
            })
    protected String firstName;

    @MultiField(mainField = @Field(index = true, type = FieldType.Text, name = "last_name", analyzer = "greek_lowercase_analyzer"),
            otherFields = {
                    @InnerField(index = true, type = FieldType.Keyword, suffix = "keyword",  normalizer = "lowercase_normalizer"),
                    @InnerField(index = true, type = FieldType.Text, suffix = "ngram", analyzer = "ngram_analyzer", searchAnalyzer = "greek_lowercase_analyzer")
            })
    protected String lastName;

    @MultiField(mainField = @Field(index = true, type = FieldType.Text, name = "email"),
            otherFields = {
                    @InnerField(index = true, type = FieldType.Keyword, suffix = "keyword",  normalizer = "lowercase_normalizer"),
                    @InnerField(index = true, type = FieldType.Text, suffix = "ngram", analyzer = "ngram_analyzer",searchAnalyzer = "greek_lowercase_analyzer")
            })
    protected String email;

    @Field(type = FieldType.Keyword, name = "status")
    protected String status;

    @Field(type = FieldType.Boolean, name = "is_enabled")
    protected Boolean isEnabled;

    @Field(type = FieldType.Boolean, name = "is_verified")
    protected Boolean isVerified;

    @Field(type = FieldType.Keyword, name = "roles_names")
    protected List<String> rolesNames;

    @Field(type = FieldType.Integer, name = "role_ids")
    protected List<Integer> roleIds;
}
