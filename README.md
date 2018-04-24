// Rest
TODO: Rest auth
TODO: More methods
TODO: Rest exceptions
TODO: Bean validation

// Security
TODO: Save number of requests per API key, instead of per IP
TODO: RememberMe login

// Client
TODO:   Prvi put: DA:   Stvara client id (GUUID) putem BLOB
                        Radi provjeru kao pod 1. NE
                        Server registrira client id pod username/api key
                        
                  NE:
                        Client salje hashani API key usera + secret + clientID + user name + request + salt
                        Server dehashira i vrsi ekstrakciju podatka
                        Provjerava Username i API key
                        Provjerava secret
                        Uzima request i obraduje ga
                        Salje odgovor
        

TODO: Korisnicke postavke za velicinu prozora, client id, etc.
TODO: Salje zathev kod svakog pokretanja za dobivanje svih kategorija
TODO: Renderanje svih kategorija i slika
TODO: Izabire kategoriju
TODO: Salje request za random pitanja
TODO: Rendera pitanje i odgovore
TODO: Vrijeme prolazi, kada prode automatski novo
TODO: Na kraju bodovi
TODO: Sharing rezultata na drustvenim mrezama