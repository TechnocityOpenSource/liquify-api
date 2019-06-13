package liquify.api.utils.dto;

public class ConversionArguments {

    private static final String[] validFileExtensions = new String[]{".xml", ".yaml", ".json", ".sql"};
    private static final String[] validDatabases = new String[]{"db2", "derby", "firebird", "h2",
            "hsql", "informix", "mssql", "mariadb", "mysql", "oracle", "postgresql", "sqlite", "asany", "sybase"};

    private String source;

    private ConversionType conversionType;

    private String database;

    public String getSource() {
        return source;
    }

    public void setSource(final String source) {
        this.source = source;
    }

    public ConversionType getConversionType() {
        return conversionType;
    }

    public void setConversionType(final ConversionType type) {
        this.conversionType = type;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(final String database) {
        this.database = database;
    }

    public boolean areValid() {
        return isValidPath(source) && hasValidType();
    }

    private boolean isValidPath(final String path) {
        return path != null && hasValidExtension(path);
    }

    private boolean hasValidExtension(final String path) {
        boolean validExtension = false;
        for (String fileExtension : validFileExtensions) {
            if (path.endsWith(fileExtension)) {
                validExtension = true;
                break;
            }
        }
        return validExtension;
    }

    public boolean hasValidType() {
        if (conversionType == null) {
            return false;
        } else if (!conversionType.equals(ConversionType.SQL)) {
            return true;
        } else if (conversionType.equals(ConversionType.SQL) && databaseIsSupported()) {
            return true;
        } else {
            return false;
        }

    }

    public boolean databaseIsSupported() {
        boolean validDatabase = false;
        if (database != null) {
            for (String db : validDatabases) {
                if (database.equals(db)) {
                    validDatabase = true;
                    break;
                }
            }
        }
        return validDatabase;
    }

    public enum ConversionType {
        XML("xml"), YAML("yaml"), JSON("json"), SQL("sql");

        ConversionType(final String extension) {
            this.extension = extension;
        }

        private String extension;

        public String getExtension() {
            return extension;
        }

        public static ConversionType fromString(final String value) {
            if ("xml".equals(value)) {
                return XML;
            } else if ("yaml".equals(value)) {
                return YAML;
            } else if ("json".equals(value)) {
                return JSON;
            } else if ("sql".equals(value)) {
                return SQL;
            } else
                return null;
        }
    }
}
