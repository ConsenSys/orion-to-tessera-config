package net.consensys.tessera.migration;

import com.quorum.tessera.config.*;
import com.quorum.tessera.config.util.JaxbUtil;
import picocli.CommandLine;

import javax.validation.*;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Set;

public class Main {

    public static void main(String... args) throws Exception {

        final CommandLine commandLine = new CommandLine(MigrateCommand.class);
        CommandLine.ParseResult parsedArgs = commandLine.parseArgs(args);
        try {
            int exitValue = commandLine.execute(args);
            Config config = commandLine.getExecutionResult();

            boolean skipValidation = parsedArgs.matchedOptionValue("skipValidation",false);

            if(!skipValidation) {
                Validator validator = Validation.byDefaultProvider()
                        .configure()
                        .ignoreXmlConfiguration()
                        .buildValidatorFactory().getValidator();
                Set<ConstraintViolation<Config>> violations = validator.validate(config);
                if (!violations.isEmpty()) {

                    violations.forEach(System.err::println);

                    JaxbUtil.marshalWithNoValidation(config,System.out);

                    System.exit(2);
                }
            }

            if (parsedArgs.hasMatchedOption("outputfile")) {
                java.nio.file.Path outputfile = parsedArgs.matchedOption("outputfile").getValue();
                Files.deleteIfExists(outputfile);
                try(OutputStream fileos = Files.newOutputStream(outputfile)) {
                    JaxbUtil.marshalWithNoValidation(config, fileos);
                    System.out.printf("Saved config to %s", outputfile.toAbsolutePath());
                    System.out.println();
                }
            } else {
                JaxbUtil.marshalWithNoValidation(config, System.out);
            }

            System.exit(exitValue);
        } catch(IOException ex) {
            System.err.println(ex.getMessage());
            if(parsedArgs.hasMatchedOption("verbose")) {
                ex.printStackTrace();
            }
        } catch (ConstraintViolationException ex) {
            ex.getConstraintViolations().forEach(System.err::println);
            System.exit(2);
        }

    }

}
