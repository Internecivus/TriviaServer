# H1 TriviaServer
TODO: Change password for admin



# H2 TL;DR
*Web sučelje se može vidjeti na:  (testno korisničko ime i lozinka su `trivia` i `trivia`)*
*Gotova klijentska aplikacija se može preuzeti [ovdje]() (Potrebno je imati instaliranu Java koja se može naći [ovdje](https://java.com/en/download/).*
*API sučelje se može vidjeti na:
    Neki primjeri upita su:
    *
    *
    *
    *
*
# H2 Opis
I. dio projekta za Objektno Programiranje 2 i Uvod u XML programiranje.
Sadrži server Trivia platforme s kojim komunicira klijentska aplikacija. Organiziran je u .EAR u kojem se nalaze 3 modula i jedna zajednička bibioloteka.

Server je namijenjen pokretanju na Wildfly 12.0.0.Final aplikacijskom serveru u Java EE 8 preview modu.

Kod klijentske aplikacije dostupan je na https://github.com/Internecivus/TriviaClient.




# H2 Upute
1. Downloadajte i instalirajte Java JDK 10 [ovdje](http://www.oracle.com/technetwork/java/javase/downloads/jdk10-downloads-4416644.html);
(Pazite da možda IDE koji koristite nema manju bundelanu verziju, u suprotnom morate promijeniti verziju ili Maven zastavice)

1. Downloadajte i instalirajte Wildfly 12.0.0.Final [ovdje](http://wildfly.org).

2. Downloadajte i instalirajte Maven [ovdje](https://maven.apache.org). (Opcionalno možete koristiti IDE koji ima integraciju s Mavenom kao što je IntelliJ)

3. U WILDFLY_FOLDER/standalone/configuration/standalone.xml
    Dodajte `<default-security-domain value="jaspitest"/>`
    Pod `<subsystem xmlns="urn:jboss:domain:ejb3:5.0">`
    Da omogućite EJBContext sigurnosne provjere.
    
    * Dodajte `<location name="/images" handler="image-content"/>`
    Unutar `<host name="default-host" alias="localhost">` pod `<subsystem xmlns="urn:jboss:domain:undertow:5.0">`
    i
    * Dodajte `<file name="image-content" path="${jboss.home.dir}/standalone/data/images"/>`
    Unutar `<handlers>` pod `<subsystem xmlns="urn:jboss:domain:undertow:5.0">`
    Da omogućite pristupanju slikama putem /images. Napomena, u produkciji se datoteke ne bi trebale spašavati u folder aplikacijskog servera.
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
        </datasource>```
    Pod `datasources` pod `<subsystem xmlns="urn:jboss:domain:datasources:5.0">`
    S time da morate zamijeniti BAZA_IME, USERNAME i PASSWORD s vašim podacima.
    Imajte na umu da je u persistence.xml datasource referenciran sa trivia_db_remote.

4. Pokrenite Wildfly sa -Dee8.preview.mode=true





