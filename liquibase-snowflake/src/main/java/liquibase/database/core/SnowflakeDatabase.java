package liquibase.database.core;

import liquibase.Scope;
import liquibase.database.AbstractJdbcDatabase;
import liquibase.database.DatabaseConnection;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.structure.DatabaseObject;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SnowflakeDatabase extends AbstractJdbcDatabase {

    public static final String PRODUCT_NAME = "Snowflake";
    private Set<String> systemTables = new HashSet<>();
    private Set<String> systemViews = new HashSet<>();

    public SnowflakeDatabase() {
        super.setCurrentDateTimeFunction("current_timestamp::timestamp_ntz");
        super.unmodifiableDataTypes.addAll(Arrays.asList("integer", "bool", "boolean", "int4", "int8", "float4", "float8", "numeric", "bigserial", "serial", "bytea", "timestamptz"));
        super.unquotedObjectsAreUppercased = true;
        super.addReservedWords(getDefaultReservedWords());
        super.defaultAutoIncrementStartWith = BigInteger.ONE;
        super.defaultAutoIncrementBy = BigInteger.ONE;
    }

    @Override
    public String getShortName() {
        return "snowflake";
    }

    @Override
    protected String getDefaultDatabaseProductName() {
        return PRODUCT_NAME;
    }

    @Override
    public Integer getDefaultPort() {
        return null;
    }

    @Override
    public Set<String> getSystemTables() {
        return systemTables;
    }

    @Override
    public Set<String> getSystemViews() {
        return systemViews;
    }

    @Override
    public int getPriority() {
        return PRIORITY_DATABASE;
    }

    @Override
    public boolean supportsInitiallyDeferrableColumns() {
        return false;
    }

    @Override
    public boolean supportsDropTableCascadeConstraints() {
        // Snowflake doesn't enforce constraints, so statement won't throw error, but won't drop table as well
        // https://docs.snowflake.com/en/sql-reference/constraints.html
        return true;
    }

    @Override
    public boolean isCorrectDatabaseImplementation(DatabaseConnection conn) throws DatabaseException {
        return PRODUCT_NAME.equalsIgnoreCase(conn.getDatabaseProductName());
    }

    @Override
    public String getDefaultDriver(String url) {
        if (url.startsWith("jdbc:snowflake:")) {
            return "net.snowflake.client.jdbc.SnowflakeDriver";
        }
        return null;
    }

    @Override
    public boolean supportsCatalogs() {
        return true;
    }

    @Override
    public boolean supportsCatalogInObjectName(Class<? extends DatabaseObject> type) {
        return false;
    }

    @Override
    public boolean supportsSequences() {
        return true;
    }

    @Override
    public boolean supportsTablespaces() {
        return false;
    }

    @Override
    public boolean supportsAutoIncrement() {
        return true;
    }

    @Override
    public String getAutoIncrementClause() {
        return "AUTOINCREMENT";
    }

    @Override
    protected String getAutoIncrementStartWithClause() {
        return "%d";
    }

    @Override
    protected String getAutoIncrementByClause() {
        return "%d";
    }

    @Override
    public boolean generateAutoIncrementStartWith(BigInteger startWith) {
        return true;
    }

    @Override
    public boolean generateAutoIncrementBy(BigInteger incrementBy) {
        return true;
    }

    @Override
    public boolean supportsRestrictForeignKeys() {
        return true;
    }

    @Override
    protected String getConnectionSchemaName() {
        DatabaseConnection connection = getConnection();
        if (connection == null) {
            return null;
        }
        try (ResultSet resultSet = ((JdbcConnection) connection).createStatement().executeQuery("SELECT CURRENT_SCHEMA()")) {
            resultSet.next();
            return resultSet.getString(1);
        } catch (Exception e) {
            Scope.getCurrentScope().getLog(getClass()).info("Error getting default schema", e);
        }
        return null;
    }

    @Override
    public boolean supportsSchemas() {
        return true;
    }

    private Set<String> getDefaultReservedWords() {
        /*
         * List taken from
         * https://docs.snowflake.net/manuals/sql-reference/reserved-keywords.html
         */

        Set<String> reservedWords = new HashSet<>();
        reservedWords.add("ACCOUNT");
        reservedWords.add("ALL");
        reservedWords.add("ALTER");
        reservedWords.add("AND");
        reservedWords.add("ANY");
        reservedWords.add("AS");
        reservedWords.add("BETWEEN");
        reservedWords.add("BY");
        reservedWords.add("CASE");
        reservedWords.add("CAST");
        reservedWords.add("CHECK");
        reservedWords.add("COLUMN");
        reservedWords.add("CONNECT");
        reservedWords.add("CONNECTION");
        reservedWords.add("CONSTRAINT");
        reservedWords.add("CREATE");
        reservedWords.add("CROSS");
        reservedWords.add("CURRENT");
        reservedWords.add("CURRENT_TIME");
        reservedWords.add("CURRENT_TIMESTAMP");
        reservedWords.add("CURRENT_USER");
        reservedWords.add("DATABASE");
        reservedWords.add("DELETE");
        reservedWords.add("DISTINCT");
        reservedWords.add("DROP");
        reservedWords.add("ELSE");
        reservedWords.add("EXISTS");
        reservedWords.add("FALSE");
        reservedWords.add("FOLLOWING");
        reservedWords.add("FOR");
        reservedWords.add("FROM");
        reservedWords.add("FULL");
        reservedWords.add("GRANT");
        reservedWords.add("GROUP");
        reservedWords.add("GSCLUSTER");
        reservedWords.add("HAVING");
        reservedWords.add("ILIKE");
        reservedWords.add("IN");
        reservedWords.add("INCREMENT");
        reservedWords.add("INNER");
        reservedWords.add("INSERT");
        reservedWords.add("INTERSECT");
        reservedWords.add("INTO");
        reservedWords.add("IS");
        reservedWords.add("ISSUE");
        reservedWords.add("JOIN");
        reservedWords.add("LATERAL");
        reservedWords.add("LEFT");
        reservedWords.add("LIKE");
        reservedWords.add("LOCALTIME");
        reservedWords.add("LOCALTIMESTAMP");
        reservedWords.add("MINUS");
        reservedWords.add("NATURAL");
        reservedWords.add("NOT");
        reservedWords.add("NULL");
        reservedWords.add("OF");
        reservedWords.add("ON");
        reservedWords.add("OR");
        reservedWords.add("ORDER");
        reservedWords.add("ORGANIZATION");
        reservedWords.add("QUALIFY");
        reservedWords.add("REGEXP");
        reservedWords.add("REVOKE");
        reservedWords.add("RIGHT");
        reservedWords.add("RLIKE");
        reservedWords.add("ROW");
        reservedWords.add("ROWS");
        reservedWords.add("SAMPLE");
        reservedWords.add("SCHEMA");
        reservedWords.add("SELECT");
        reservedWords.add("SET");
        reservedWords.add("SOME");
        reservedWords.add("START");
        reservedWords.add("TABLE");
        reservedWords.add("TABLESAMPLE");
        reservedWords.add("THEN");
        reservedWords.add("TO");
        reservedWords.add("TRIGGER");
        reservedWords.add("TRUE");
        reservedWords.add("TRY_CAST");
        reservedWords.add("UNION");
        reservedWords.add("UNIQUE");
        reservedWords.add("UPDATE");
        reservedWords.add("USING");
        reservedWords.add("VALUES");
        reservedWords.add("VIEW");
        reservedWords.add("WHEN");
        reservedWords.add("WHENEVER");
        reservedWords.add("WHERE");
        reservedWords.add("WITH");

        return reservedWords;
    }
}
