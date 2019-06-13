package liquify.api.service;

import liquibase.changelog.ChangeLogParameters;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.exception.LiquibaseException;
import liquibase.parser.ChangeLogParser;
import liquibase.parser.ChangeLogParserFactory;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.ResourceAccessor;
import liquibase.serializer.ChangeLogSerializer;
import liquibase.serializer.ChangeLogSerializerFactory;
import liquify.api.utils.dto.ConversionArguments;

import javax.inject.Singleton;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Singleton
public class LiquifyService {

    /**
     * @param conversionArguments
     * @return
     * @throws IOException
     * @throws LiquibaseException
     */
    public OutputStream liquifyDatabaseChangeLog(final ConversionArguments conversionArguments) throws IOException, LiquibaseException {

        if (!conversionArguments.areValid()) {
            throw new RuntimeException("Invalid conversion arguments");
        }

        return convertDatabaseChangeLog(conversionArguments);
    }

    /**
     * @param conversionArguments
     * @return
     * @throws IOException
     * @throws LiquibaseException
     */
    private OutputStream convertDatabaseChangeLog(final ConversionArguments conversionArguments) throws IOException, LiquibaseException {
        String targetFileName = buildFilename(conversionArguments);
        try {
            ResourceAccessor resourceAccessor = new ClassLoaderResourceAccessor();
            ChangeLogParser parser = ChangeLogParserFactory.getInstance().getParser(conversionArguments.getSource(), resourceAccessor);
            DatabaseChangeLog changeLog = parser.parse(conversionArguments.getSource(), new ChangeLogParameters(), resourceAccessor);
            ChangeLogSerializer serializer = ChangeLogSerializerFactory.getInstance().getSerializer(targetFileName);

            // make sure only changesets targeting provided database are serialized
            List<ChangeSet> changeSets = new LinkedList<>();

            for (ChangeSet set : changeLog.getChangeSets()) {
                Set<String> dbmsSet = set.getDbmsSet();
                if (dbmsSet == null || dbmsSet.isEmpty() || dbmsSet.contains(conversionArguments.getDatabase())) {
                    changeSets.add(set);
                }
            }

            for (ChangeSet set : changeSets) {
                set.setFilePath(targetFileName);
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            serializer.write(changeSets, baos);
            return baos;
        } finally {
            deleteTargetFile(targetFileName);
        }
    }

    /**
     * @param targetFileName
     */
    private static void deleteTargetFile(final String targetFileName) {
        try {
            Files.deleteIfExists(Paths.get(targetFileName));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * @param arguments
     * @return
     */
    private static String buildFilename(final ConversionArguments arguments) {
        String source = arguments.getSource();
        String baseFileName = source.substring(0, source.lastIndexOf("."));
        if (arguments.getConversionType().equals(ConversionArguments.ConversionType.SQL)) {
            return String.format("%s.%s.%s", baseFileName, arguments.getDatabase(),
                    arguments.getConversionType().getExtension());
        } else {
            return String.format("%s.%s", baseFileName, arguments.getConversionType().getExtension());
        }
    }
}
