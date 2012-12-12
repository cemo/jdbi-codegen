package com.digitolio.jdbi.codegen.codemodel;

import com.digitolio.jdbi.auto.SqlSupport;
import com.digitolio.jdbi.table.Table;
import org.skife.jdbi.v2.Binding;

/**
 * SELECT * FROM BASIC_PROFILE WHERE DIRTY = 1 LIMIT :limit
 *
 * @author Cemo
 */
public class SqlDirtyGet extends  SqlSupport{

    private String clause;

    public SqlDirtyGet(Table table) {
        super(table);
        initConstantSqls();
    }

    private void initConstantSqls() {
        clause = initAllClause();
    }

    private String initAllClause() {
        return initUpdatePart() + initWherePart();
    }

    private String initUpdatePart() {
       return "SELECT * FROM ".concat(table.getTableName());
    }

    private String initWherePart() {
       return " WHERE DIRTY = 1 LIMIT :limit";
    }

    @Override
    public String generate(Binding params) {
        return clause;
    }
}