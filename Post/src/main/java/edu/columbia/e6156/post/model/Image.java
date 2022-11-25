package edu.columbia.e6156.post.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.UUID;


@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public final class Image {
    @Type(type="org.hibernate.type.UUIDCharType")
    private UUID userId;

    @Id
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID imageId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
}
