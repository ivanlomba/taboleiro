package taboleiro.model.service.xade;

import java.util.List;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@Value
@JacksonXmlRootElement(localName = "importarCualificacions")
@Builder
public class Xade {

    @JacksonXmlProperty(localName = "codigoGrupo")
    private String groupCode;

    @JacksonXmlProperty(localName = "codigoAvaliacion")
    private String evaluation;

    @JacksonXmlProperty(localName = "codigoMateriaAvaliable")
    private String codigoMateriaAvaliable;

    @Singular
    @JacksonXmlProperty(localName = "cualificacionsAlumnos")
    private List<XadeStudent> xadeStudents;

}