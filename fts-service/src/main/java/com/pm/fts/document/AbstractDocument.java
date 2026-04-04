package com.pm.fts.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@Setting(settingPath = "elasticsearch-settings.json")
public class AbstractDocument implements Serializable {
    private static final long serialVersionUID = -6846687739343473149L;

    @Id
    @Field(type = FieldType.Integer, name = "id")
    protected Integer id;

    @Field(type = FieldType.Keyword, name = "public_id")
    protected String publicId;

    protected AbstractDocument(AbstractDocument abs){
        this.setId(abs.getId());
        this.setPublicId(abs.getPublicId());
    }
}
