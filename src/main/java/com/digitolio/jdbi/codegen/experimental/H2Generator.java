package com.digitolio.jdbi.codegen.experimental;

import com.digitolio.jdbi.codegen.Scanner;
import com.digitolio.jdbi.strategy.SnakeCaseTranslatingStrategy;
import com.digitolio.jdbi.table.Column;
import com.digitolio.jdbi.table.Table;
import com.digitolio.jdbi.table.TableResolver;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;
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
            Object generate = h2Generator.generate();
            String content = generate.toString();
            File file = new File(args[1].concat("/db/h2/").concat(resolve.getTableName().toLowerCase().concat(".ddl")));
            Files.createParentDirs(file);
            BufferedWriter bufferedWriter = Files.newWriter(file,
                                                            Charset.defaultCharset());
            bufferedWriter.write(content);
            bufferedWriter.close();
        }


    }


    private Object generate() {
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

        builder.append("\t,primary key(");
        List<String> l = Lists.newArrayList();
        for (Column column : table.getPrimaryKeyColumns()) {
            l.add(column.getDatabaseName());
        }
        builder.append(Joiner.on(", ").join(l));
        builder.append(")");

        builder.append("\n);");
//        System.out.println(builder.toString());
        return builder.toString();
    }

    private String getNullInfo(Column column) {
        com.digitolio.jdbi.annotations.Column annotation = column.getField().getAnnotation(
                com.digitolio.jdbi.annotations.Column.class);
        boolean nullable = false;
        if (annotation != null) {
            nullable = annotation.nullable();
        }
        if (nullable) {
            return " null ";
        } else {
            return " not null ";
        }
    }

    private String getDbType(Column column) {
        Class<?> clazz = column.getField().getType();
        if (Integer.class.isAssignableFrom(clazz)) {
            return "int";
        } else if (Long.class.isAssignableFrom(clazz)) {
            return "bigint";
        } else if (String.class.isAssignableFrom(clazz)) {
            return "varchar(128)";
        }else if (Date.class.isAssignableFrom(clazz)) {
            return "datetime";
        }else if (Enum.class.isAssignableFrom(clazz)) {
            return "varchar(128)";
        }

        return "doktor bu ne";

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
