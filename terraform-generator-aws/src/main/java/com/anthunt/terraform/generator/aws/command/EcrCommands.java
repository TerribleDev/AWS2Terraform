package com.anthunt.terraform.generator.aws.command;

import com.anthunt.terraform.generator.aws.command.args.CommonArgs;
import com.anthunt.terraform.generator.aws.service.ecr.ExportEcrRepositories;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import software.amazon.awssdk.services.ecr.EcrClient;

import javax.validation.Valid;

@Slf4j
@ShellComponent
public class EcrCommands extends AbstractCommands {

    private ExportEcrRepositories exportEcrRepositories;

    public EcrCommands(ExportEcrRepositories exportEcrRepositories) {
        this.exportEcrRepositories = exportEcrRepositories;
    }

    @ShellMethod("Export terraform resources of ECR Repository.")
    public void ecrRepository(@ShellOption(optOut = true) @Valid CommonArgs commonArgs) {
        exportEcrRepositories.exportTerraform(EcrClient.class, commonArgs);
    }

}
