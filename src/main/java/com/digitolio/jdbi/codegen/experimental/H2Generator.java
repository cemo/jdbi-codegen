package com.digitolio.jdbi.codegen.experimental;

import com.digitolio.jdbi.codegen.Scanner;
import com.digitolio.jdbi.strategy.SnakeCaseTranslatingStrategy;
import com.digitolio.jdbi.table.Column;
import com.digitolio.jdbi.table.Table;
import com.digitolio.jdbi.table.TableResolver;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Set;

/**
 * @author C.Koc
 */
public class H2Generator {

    private final Table table;
    private final Class<?> clazz;
    private final File targetDir;

    public H2Generator(Table table, Class<?> clazz, File targetDir) {
        this.table = table;
        this.clazz = clazz;
        this.targetDir = targetDir;
    }

    public static void main(String[] args) throws IOException {
        SnakeCaseTranslatingStrategy strategy = new SnakeCaseTranslatingStrategy();
        TableResolver tableResolver = new TableResolver();

//        Set<Class<?>> classes = new Scanner().scanPackage("com.digitolio.jdbi.codegen.test");
        Set<Class<?>> classes = new Scanner().scanPackage(args[0]);
//        File targetDir = new File("D:\\PersonalProjects\\digitolio\\jdbi-codegen\\src\\main\\java\\cemo");
        File targetDir = new File(args[1]);

        for (Class<?> aClass : classes) {
            Table resolve = tableResolver.resolve(aClass, strategy);
            H2Generator h2Generator = new H2Generator(resolve, aClass, targetDir);
            h2Generator.generate();
        }
    }


    private void generate() {
        StringBuilder builder = new StringBuilder();
        builder.append("drop table ").append(table.getTableName()).append(" if exists;\n");
        builder.append("create table ").append(table.getTableName()).append("(\n");
        int a = 0;
        for (Column column : table.getAllColumns()) {
            builder.append("\t");
            if(a++>0){builder.append(",");}
            builder.append(column.getDatabaseName())
                   .append(" ")
                   .append(getDbType(column))
                   .append(getNullInfo(column))
                   .append("\n");
        }
        builder.append(")\n");
        System.out.println(builder.toString());
    }

    private String getNullInfo(Column column) {
        return " not null ";
    }

    private String getDbType(Column column) {
        Class<?> clazz = column.getField().getType();
        if (Integer.class.isAssignableFrom(clazz)) {
            return "int";
        } else if (Long.class.isAssignableFrom(clazz)) {
            return "bigint";
        } else if (String.class.isAssignableFrom(clazz)) {
            return "varchar(20)";
        }else if (Date.class.isAssignableFrom(clazz)) {
            return "datetime";
        }else if (Enum.class.isAssignableFrom(clazz)) {
            return "varchar(20)";
        }

        return "bu ne amk";

    }

/*
    drop table shopping_cart_item if exists;
    create table shopping_cart_item (
       order_id       int        not null
       ,user_id   int not null
       ,item_count   int not null
       ,item_price   int not null
       ,primary key(user_id, order_id)
    );

    drop table product_order if exists;
    create table product_order (
       order_id    int        not null  primary key auto_increment
       ,user_id   int not null
       ,order_type   varchar(20) not null
       ,order_time datetime not null
       ,process_time datetime not null
       ,activate_time datetime not null
       ,refund_time datetime not null
    );
*/

}
