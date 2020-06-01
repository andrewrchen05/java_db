# java_db

Until I figure out how to do remote development with vscode, here is the following protocol:

1. Clone repository onto your machine
2. Edit files on vscode or any other visually pleasing interface
3. Push/pull to run repo on lab machine

Below are my personal notes, watch the solution recording first.

### Importing Files

```bash
scp -r phase3setup/ lab:/tmp/achen134

#ssh into lab-machine one more time
```

### Starting Database

At this step, you must run the postgre scripts in the postgres folder

I have renamed the files to make starting and stopping easier

```bash
cat postgre.sh

chmod +x postgre.sh #if you haven't done this yet

./postgre.sh

bash createdb.sh project # this is what i named the databse

# at this point we can edit our java files
```

### Compiling Java

```bash
# cd into java folder

bash compile.sh

./run.sh project $PGPORT $USER # look in script for explanation

# Here we should be able to see the main menu with 15 options
```
