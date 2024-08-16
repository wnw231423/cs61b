# Gitlet Design Document

**Name**: wnw231423

## Classes and Data Structures

### Main
Entry of our program. It takes in arguments from shell the command line 
and call the corresponding command in `Repository`. It also validates the
arguments by its length(size).

#### Fields
No fields.


### Repository
The main logic of our program, which basically do these things:
1. Give access to file system so that we can manipulate needed file or dir.
2. Do or call corresponding method based on the command user inputs.

#### Fields
Directory and file variables, they are all `public static final`:
   1. `CWD`, current working directory
   2. `GITLET_DIR`, our gitlet repo, includes following things:
      1. `STAGE`, stage file stores the stage information.
      2. `COMMIT_DIR`, dir that saves commits.
      3. `BRANCH_DIR`, dir that saves head and other branch pointer including master.
         1. `HEAD_POINTER`, head pointer which store the hash code of head commit.
         2. `MASTER_POINTER`, master pointer which store the hash code of master commit.
      4. `BLOBS_DIR`, dir that saves file copies from cwd.


### Commit
The commit object of our program. It has some instance variables and offers
some methods so that we can do corresponding operation. Commits are serialized within
the COMMIT

#### Fields
1. `private String message`, the commit message of this commit.
2. `private String parentCode`, the hash code of its parent commit.
3. `private Commit transient parent`, the parent commit of this one.
4. `private String parent2Code`, the hash code of its another commit, only for merged commits.
5. `private TreeMap<String, String> trackedFiles`, the tracked files of this commit, mapping
    between file name and this file's hash code.
6. `private Date commitDate`, when this commit was made.

### Stage
The stage object of our program. It represents the stage area where we add and remove file for
commits. There is only one stage instance and it's serialized in the stage file.

#### Fields
1. `private TreeMap<String, String> trackedList`, tracked files map which is inherited from head commit.
2. `private TreeMap<String, String> addList`, map for `add` command.
3. `private TreeMap<String, String> removeList`, map for `rm` command. //TODO



## Algorithms
1. For `stage`, In my implementation, the stage will always inherit tracked files from head commit,
   every time it clear stage for `commit`, it will combine tracked files and files in stage,
   store it and return it for construct the next newly head commit. In this way, during `add` command,
   it only looks into the stage and compare if add command validate, no need to go to commit obj.



## Persistence
The directory structure:
```
CWD                             <==== Whatever the current working directory is
└── .gitlet                     <==== All persistant data is stored within here
    ├── stage                   <==== Where the stage is stored (a file)
    ├── commit                  <==== All ccommits are stored in this directory
    │   ├── commit1             <==== A single commit instance stored to a file
    │   ├── commit2
    │   ├── ...
    │   └── commitN
    ├── branch                  <==== All pointers are stored in this directory
    │   ├── head 
    │   ├── master
    │   └── ...
    └── blob                    <==== All file copies are stored in this directory
        ├── blob1 
        ├── blob2
        ├── ...
        └── blobN
```
The `Repository` will set up all persistence. It will:
1. Create the `.gitlet` dir if it doesn't already exist.
2. Create the `commit`, `branch`, `blob` dir.
3. make one init commit, create `stage`, and store the initial commit in `commit` dir.

When `add` command is used, we do the following thing:
1. Deserialize the stage into our program.
2. `public void addFile(String fileName)` of stage is called, which update the status.
3. `public void updateStatus()` of stage is called, which serialize the new stage into file.

When `commit` command is used, we do the following thing:
1. deserialize the stage into our program.
2. `public TreeMap<String, String> clearStage` of stage is called, which clear the stage and
   update and serialize stage into file, return the ultimate file tracked list for the new commit.
   It also copies the new files into `blob` dir.
3. Construct the new commit using the TreeMap above and serialize it into `commit` dir.
