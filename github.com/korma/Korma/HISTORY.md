0.3.0-RC6
---------
* Options for IdleConnectionTestPeriod and TestConnectionOnCheckin (federkasten)
* defonce config options to prevent issues with MySQL delimiters (cosmi)
* README cleanup (writa)
* Can now run a test query on checkout for connection verification (joekarl)
* Introduced alias-delimiter option to choose word for alias (ktsujister and Ringman)
* Fixed issue with set-fields not applying db delimiters (mjtodd)
* Made korma.sql.engine/bind-params public for incubator (sgrove)

0.3.0-RC5
---------

* support nested `korma.db/transaction` calls (josephwilk)
* integrated with TravisCI

0.3.0-RC4
---------

* MS Access db config helper function (Vadim Geshel)
* bugfix in `do-query` w/ options (RingMan)

0.3.0-RC3
---------

* .... lost track, sorry, too lazy to backfill the history here, at least today

0.3.0-RC2
---------

* Connection pool always returns a connection pool, use :make-pool? option to disable pool creation

0.3.0-RC1
---------

* Allow opting out of the connection pool creation
* Allow sending other kinds of sb specs straight through to java.jdbc

0.3.0-beta15
---------------------

*  transactional wrapping for multiple databases (Timo Westkämper)
*  fixing macro expansion bug introduced in recent beta (Moritz Heidkamp, Joshua Eckroth)
*  Can use mysql/count so that (count :*) works correctly on MySQL (Tsutomu YANO)
*  Added `union`, `union-all`, and `intersect` queries

0.3.0-beta14
------------

*  Support for `many-to-many` relationships (Dennis Roberts)
*  Fixed table-name delimiting, and at the same time, support Postgres' schema and 
   queries covering multiple databases.  
   See: https://github.com/korma/Korma/pull/105 (Tsutomu YANO)

0.3.0-beta13
------------

*   Set Min/Max Connection Pool Size from db spec (Nick Zalabak)
*   Merge defaults instead of overwriting them (Jim Crossley)
*   DB specs can reference existing datasources or JNDI references (Jim Crossley)
*   Added `between` predicate (Charles Duffy)
*   Corrected default port for MS SQL Server (Alexander Zolotko)
*   Added basic `having` support (Mike Aldred)
*   Added `korma.db/h2` to create a database specification for a h2 database (Juha Syrjälä)
*   Insert statements with empty values turn into SQL of "DO 0", a NOOP
*   Empty where clauses are ignored, instead of creating illegal SQL

*started on Dec 27, 2012*

