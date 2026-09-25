package com.sistemadecadastramento.infra.config;

import com.sistemadecadastramento.dtos.ClienteRequestDto;
import com.sistemadecadastramento.models.TipoPessoa;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ClienteDocumentoValidator implements ConstraintValidator<CpfOuCnpjValido, ClienteRequestDto>{

    @Override
    public boolean isValid(ClienteRequestDto dto, ConstraintValidatorContext context) {
        
        if(dto.getTipoPessoa() == null || dto.getDocumento() == null){
            return true;
        }

        String documento = dto.getDocumento().replaceAll("[^0-9]", "");

        if(dto.getTipoPessoa() == TipoPessoa.FISICA){
            return isValidCpf(documento);
        } else if(dto.getTipoPessoa() == TipoPessoa.JURIDICA){
            return isValidCnpj(documento);
        }
        return false;
    }

    private boolean isValidCpf(String cpf){
        return cpf.length() == 11;
    }

    private boolean isValidCnpj(String cnpj){
        return cnpj.length() == 14;
    }
    
}
