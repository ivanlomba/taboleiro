package taboleiro.model.service.xade;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class XadeStudent {

    @JacksonXmlProperty(localName = "codigoAlumno")
    private String studentCode;

    @JacksonXmlProperty(localName = "codigoCualificacion")
    private String gradeCode;

    @JacksonXmlProperty(localName = "nomeAlumno")
    private String studentName;
}
