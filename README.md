# bench-es

Quick and dirty benchmarks for clojure elasticsearch clients

## Usage

Takes a big chunk of texts, splits it in 140 words chunks, indexes each chunk,
gets each doc from index, searches for the 25th word of each chunk.

On my machine with sample text document (War and Peace: resources/pg2600.txt => 4039 docs):

```
# run with clj-elasticsearch version 0.4.0
lein with-profile new run resources/pg2600.txt native
#=> ~9s

# run with clj-elasticsearch version 0.3.3
lein with-profile old run resources/pg2600.txt native
#=> ~9s

# run with rest client (elastish version 1.0.2)
lein with-profile new run resources/pg2600.txt rest
#=> ~51s
```

## License

Copyright © 2013 Nils Grunwald

Distributed under the Eclipse Public License, the same as Clojure.
