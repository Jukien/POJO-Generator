#### Changelog

##### Version ${parent.version} - Published on ${timestamp}
###### New
- Microsoft SQL Server Database support (GitHub [#27](https://github.com/Jukien/POJO-Generator/issues/27))

***

##### Version 2.4.0 - Published on 31/03/2023
###### Fixed
- NoClassDefFoundError with IDEA 2023.1 (GitHub [#26](https://github.com/Jukien/POJO-Generator/issues/26))

***

##### Version 2.3.1 - Published on 12/11/2020
###### Fixed
- Compatibility with JBR 8 (GitHub [#22](https://github.com/Jukien/POJO-Generator/issues/22))

***

##### Version 2.3.0 - Published on 27/10/2020
###### New
- Possibility to generate composite primary key (GitHub [#17](https://github.com/Jukien/POJO-Generator/issues/17))
- H2 Database support (GitHub [#18](https://github.com/Jukien/POJO-Generator/issues/18))

***

##### Version 2.2.1 - Published on 27/07/2020
###### New
- GitHub [#16](https://github.com/Jukien/POJO-Generator/issues/16): Add schema name on entity

***

##### Version 2.2.0 - Published on 12/04/2020
###### New
- GitHub [#11](https://github.com/Jukien/POJO-Generator/issues/11): Add compatibility with IntelliJ IDEA 2020.1 (Ultimate Edition)
- GitHub [#12](https://github.com/Jukien/POJO-Generator/issues/12): Add possibility to see the differences if the file already exists
- GitHub [#13](https://github.com/Jukien/POJO-Generator/issues/13): Add `@Column`'s length attribute
- GitHub [#14](https://github.com/Jukien/POJO-Generator/issues/14): Add new default mapping types

***

##### Version 2.1.0 - Published on 06/01/2020
###### New
- GitHub [#1](https://github.com/Jukien/POJO-Generator/issues/1): Ask only once the path to store files when we select multiple tables for generation
- GitHub [#6](https://github.com/Jukien/POJO-Generator/issues/6): Keep folders location for entities and DTOs files
- GitHub [#8](https://github.com/Jukien/POJO-Generator/issues/8): Add `@ManyToOne` and `@JoinColumn` annotations on columns with foreign key
- GitHub [#9](https://github.com/Jukien/POJO-Generator/issues/9): Add the ability to customize the prefix and suffix of file names

###### Fixed
- GitHub [#7](https://github.com/Jukien/POJO-Generator/issues/7): Error when the file already exists

***

##### Version 2.0.1 - Published on 29/11/2019
###### New
- GitHub [#4](https://github.com/Jukien/POJO-Generator/issues/4): Add compatibility with IntelliJ IDEA 2019.3 (Ultimate Edition)
- \[Add] New default mapping types

###### Fixed
- GitHub [#5](https://github.com/Jukien/POJO-Generator/issues/5): Settings are not saved at project level

***

##### Version 2.0.0 - Published on 16/09/2019
###### New
- \[Add] Possibility to customize the JPA mapping

###### Fixed
- GitHub [#2](https://github.com/Jukien/POJO-Generator/issues/2): Settings for New Projects were not saved

***

##### Version 1.1.0 - Published on 30/07/2019
###### New
- \[Add] ORACLE datatype : CHAR (with columnDefinition)
- \[Add] Possibility to add `@GeneratedValue` annotation over column which have auto increment sequence (Tested with MySQL)
- \[Modification] Compatible from 2019.1

***

##### Version 1.0.1 - Published on 18/07/2019
###### New
- \[Add] MySQL datatype : date
- \[Add] MySQL datatype : datetime
- \[Modification] The plugin will be only compatible for Java IDE

###### Fixed
- When generate a DTO file, the path choosed by the user wasn't take in count

***

##### Version 1.0.0 - Published on 08/07/2019
###### New
- \[Add] DataType
- \[Add] MySQL Database support
- \[Add] Oracle Database support
- \[Improvement] When database is not supported, actions are disabled
- \[Improvement] When we generate a POJO, the directory choosing window position the user where he was the last time
- \[Improvement] If the user select an element which is not a table, actions are disabled

***

##### Version 1.0.0-alpha-2 - Published on 30/04/2019
###### New
- Can generate a Data Transfert Object from database table
