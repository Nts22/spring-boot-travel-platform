package com.ptirado.nmviajes.dto.api.request;

import static com.ptirado.nmviajes.constants.ValidationConstants.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactoRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = CONTACTO_NOMBRE_MIN, max = CONTACTO_NOMBRE_MAX,
            message = "El nombre debe tener entre " + CONTACTO_NOMBRE_MIN + " y " + CONTACTO_NOMBRE_MAX + " caracteres")
    private String nombre;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo debe tener un formato valido")
    @Size(max = CONTACTO_EMAIL_MAX,
            message = "El correo debe tener maximo " + CONTACTO_EMAIL_MAX + " caracteres")
    private String email;

    @Size(max = CONTACTO_TELEFONO_MAX,
            message = "El telefono debe tener maximo " + CONTACTO_TELEFONO_MAX + " caracteres")
    @Pattern(regexp = TELEFONO_PATTERN + "|^$",
            message = "El telefono debe contener solo numeros, espacios, guiones y puede iniciar con +")
    private String telefono;

    @NotBlank(message = "El mensaje es obligatorio")
    @Size(min = CONTACTO_MENSAJE_MIN, max = CONTACTO_MENSAJE_MAX,
            message = "El mensaje debe tener entre " + CONTACTO_MENSAJE_MIN + " y " + CONTACTO_MENSAJE_MAX + " caracteres")
    private String mensaje;
}