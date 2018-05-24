#H1 TriviaServer
TODO: Change password for admin


#H2 TL;DR
* **Web sučelje se može vidjeti na:  (testno korisničko ime i lozinka su `trivia` i `trivia`)**

* **Gotova klijentska aplikacija se može preuzeti [ovdje]() (potrebno je imati instaliranu Java koja se može naći [ovdje](https://java.com/en/download/)).**

* **API sučelje se može vidjeti na:**

    Neki primjeri upita su:
    *
    *
    *
    *

#H2 Opis
Ovo je prvi dio projekta za Objektno Programiranje 2 i Uvod u XML programiranje.
Sadrži server Trivia platforme s kojim komunicira klijentska aplikacija. Organiziran je u .EAR u kojem se nalaze 3 modula i jedna zajednička bibioloteka.

Server je namijenjen pokretanju na Wildfly 12.0.0.Final aplikacijskom serveru u Java EE 8 preview modu.

Kod klijentske aplikacije dostupan je na [GitHubu](https://github.com/Internecivus/TriviaClient).




#H2 Upute
1. Downloadajte i instalirajte [Java JDK 10](http://www.oracle.com/technetwork/java/javase/downloads/jdk10-downloads-4416644.html)
(pazite da možda IDE koji koristite nema manju bundelanu verziju, u suprotnom morate promijeniti verziju ili Maven zastavice).

1. Downloadajte i instalirajte [Wildfly 12.0.0.Final](http://wildfly.org).

2. Downloadajte i instalirajte [Maven](https://maven.apache.org). (Opcionalno možete koristiti IDE koji ima integraciju s Mavenom kao što je IntelliJ)

3. U WILDFLY_FOLDER/standalone/configuration/standalone.xml:
    
    * dodajte `<default-security-domain value="jaspitest"/>` pod `<subsystem xmlns="urn:jboss:domain:ejb3:5.0">` da omogućite EJBContext sigurnosne provjere.
    
    * dodajte `<location name="/images" handler="image-content"/>`
    unutar `<host name="default-host" alias="localhost">` pod `<subsystem xmlns="urn:jboss:domain:undertow:5.0">`
    i
    * dodajte `<file name="image-content" path="${jboss.home.dir}/standalone/data/images"/>`
    unutar `<handlers>` pod `<subsystem xmlns="urn:jboss:domain:undertow:5.0">`
    da omogućite pristupanju slikama putem `/images`. *Napomena: u produkciji se datoteke ne bi trebale spašavati u folder aplikacijskog servera*.
    * Dodajte vlastiti datasource po sljedećem principu:
        ```<datasource jndi-name="java:jboss/datasources/trivia_db_remote pool-name="trivia_db_local">
            <connection-url>jdbc:mysql://localhost:3306/BAZA_IME</connection-url>
            <driver>mysql</driver>
            <pool>
                <min-pool-size>10</min-pool-size>
                <max-pool-size>20</max-pool-size>
                <prefill>true</prefill>
            </pool>
            <security>
                <user-name>USERNAME</user-name>
                <password>PASSWORD</password>
            </security>
        </datasource>
    pod `<datasources>` pod `<subsystem xmlns="urn:jboss:domain:datasources:5.0">`, s time da morate zamijeniti `BAZA_IME`, `USERNAME` i `PASSWORD` s vašim podacima.
    Imajte na umu da je u `persistence.xml` datasource referenciran sa `trivia_db_remote`.

4. S obzirom na to da je kolona `slike` za entitet `Kategorija` označena sa `NOT NULL`, a na vašem lokalnom računalu nema slika koje su referencirane u `TriviaPersistence/src/main/resources/META-INF/sql/data.sql`, morate ili onemogućiti dodavanje SQL podataka (u `persistence.xml`) ili vrijednosti slika postaviti u `NULL` te promijeniti tu kolonu u `NULLABLE` (`TriviaPersistence/src/main/resources/META-INF/sql/create.sql`)

5. Pokrenite Wildfly sa `-Dee8.preview.mode=true`.




