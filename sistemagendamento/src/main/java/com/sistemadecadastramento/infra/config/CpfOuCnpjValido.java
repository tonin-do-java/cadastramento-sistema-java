package com.sistemadecadastramento.infra.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;


@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ClienteDocumentoValidator.class})
public @interface CpfOuCnpjValido {
    String message() default "O documento informado não é valido para o tipo de cliente selecionado.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
