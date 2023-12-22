# BadFlandreSearchEngine


<img src="./.github/patchouli.gif" />

ヴワル魔法図書館 | 伏瓦鲁魔法图书馆 | Voile, the Magic Library


#### A simple search engine demo.

# Environment
``jre = 1.8``

# Usage

there are 3 commands: `extract`, `index` and  `server`, all require a config file named `config.properties`, which looks like:

```properties
mirror.path=your mirror path
files.path=your summarized files location
index.path=your indexes files location
```

- `mirror.path`: folder that contains html pages, it's sub path should match it's url, usually got from spider.
- `files.path`: after running the extract command, it will automatically create to store summarized files.
- `index.path`: after running the index command, it will automatically create to store indexed files according to the summarized files in `files.path`.

1. extract

    this step aiming to summarize the html content, it will collect `title`, `url` and `content` of given html files in a folder(`mirror.path`),
    
    then put the summarized file into `files.path` folder, naming it with md5.
    
    ```shell
    java -jar BadFlandreSearchEngine-0.0.1.jar extract -config config.properties
    ```

2. index
    
   this step is going to generate a `.cfs` file using the summarized files in `files.path`, as it's name, it is an index of bundle `titile`, `url`, `content` of given html files, provided for querying.
    ```shell
    java -jar BadFlandreSearchEngine-0.0.1.jar index -config config.properties
    ```
3. server
    
   this will start a http server to hold a query service, using the data from give html files.
    ```shell
    java -jar BadFlandreSearchEngine-0.0.1.jar server -config config.properties
    ```
    
    extra options: 
    - `-port port`: specific the server port.  

# Build

run the command below to build a runnable jar.
```shell
gradlew jar 
```

# Tech Stack
   
   - Vue
   - MDUI
   - Vert.x
   - htmlparser
   - apache lucene
   - ...

# Thanks

 - [whiterasbk](https://github.com/whiterasbk)
 - [Source Han Serif](https://github.com/adobe-fonts/source-han-serif)