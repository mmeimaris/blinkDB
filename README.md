# axonDB
The source code and queries for axonDB 

**Note: this this the source code used for the experiments presented in our paper that is currently under the submission and reviewing process of ICDE 2017

The package **/*/api contains two key classes for loading and querying into axonDB. Specifically, BigLoader.java is used 
for RDF loading, and currently has been tested with .nt and .rdf/xml serializations. Quads are not supported at the moment.
Its main function can be called with three parameters, representing the path where the new blinkDB instance should be stored on disk, 
the name of the axonDB instance, and the path to the RDF file.
The BigQueryTests.java class can be used to initialize an existing axonDB instance and run a series of queries on it. The queries are 
defined in Queries.java, and can also be found in the /queries folder inside this repository.

This project is under development. For more information please contact Marios Meimaris (m.meimaris@gmail.com) or George Papastefanatos
 (gpapastefanatos@gmail.com) . 
