// Rest
TODO: Rest BasicAuth
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