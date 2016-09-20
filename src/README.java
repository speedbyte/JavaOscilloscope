/**
* A Readme for the SensorOscilloscope project
*/

/*
GIT Introduction for Windows 10
Here's my short - definitely incomplete - git introduction.

Download a command line version of git from https://git-for-windows.github.io/

Go to your preferred project folder, rightlick and open git bash.

Type 'git clone https://atreus.informatik.uni-tuebingen.de/Programmierprojekt2016/SensorOscilloscope-Java.git' or the
equivalent of your GitLab installation to create a new local repository for this project. 
Now you can open the project in eclipse by creating a new Java Project, Import > Git > Projects from Git > Existing 
local repository. 

If your team member and you are on the same branch, it's a good idea to 
'git pull' before working on the project. This ensures, you have the latest version from git.

If you make any change to any project file, eclipse will recognize this and show a '>' in front of the filename in your 
package explorer. If you made a change and want to make it available to all other project members do this.

'git add --all'
This adds all changed files to your new commit. You can also type a filename instead of '--all' to only add that file,
if you wish. 
'git commit -m "Type your commit message here"'
Type in a meaningful commit message, what you did, what improvements you made, etc. This will help your team members
and your future self to identify a project status from a commit message, should you ever want to roll back to a certain
version of the project. 
'git push'
To complete the commit, push the changes to the Git repository. 

This commit is a snapshot of the project you can go back to at any time. Before making a big change, it's probably
a good idea to save your current (hopefully working) changes, and commit them, so you can go back, should your big 
change fail somehow. This also makes copying the complete project on your local drive unnecessary.

If you wish to check out *hint, hint* an old commit, use GitLab (or the command line) to find the old commit and its
hash.

'git checkout 47b62a1df135e48c3967a1750bf84043b544025f' or your equivalent commit hash.
Now open your IDE and refresh the project. The project is now back on this commit.

To go back to your latest commit:
'git checkout TeamGui2016' or your equivalent branch.  

If you work on this project on two different machines, this might be helpful for you.
Say you've done a change on one system #1, but haven't commited it. Now you make a change on your other system #2, and
push it to Git.
Next time you're on system #1, you just want to discard any changes you've made, and pull the version from git
However, git itself will notify you, that there are changes on your system, that will be lost. If you want that:
'git stash' 
to save any changes you made locally. Now you're project is on the status of the commit you started working on. 
'git stash drop'
to delete the stash. 

Now pull from git and you're on the latest version.
 */

/*
MAVEN INSTALLATION for Windows 10

Visit https://maven.apache.org/ for the latest maven package, download and install.
On Windows, rightlick on the start button, go to system and 'erweiterte systemeinstellungen' and environment variables.
On system variables, add 'C:\apache-maven-3.3.9\bin' or your equivalent path to your maven installation to the PATH 
variables. 

Also make sure, you have a valid JAVA_HOME variable set.

Open a new cmd anywhere on your system, and 'mvn -version' should work. Pro-tip: Holding shift and right clicking
on a folder opens a sligthly different context menu, which lets you open a command line at this path, so you dont
have to cd there.
Do this on your clone of the SensorOscilloscope and you can now run mvn commands.
  
'mvn compile'
Compiles the project to the target folder

'mvn test'
Runs all tests in src/scope/test via the surefire plugin. This directory can be changed in the pom.xml. Only classes that match the default
test naming convention are executed, which can be found here: https://maven.apache.org/surefire/maven-surefire-plugin/examples/inclusion-exclusion.html







*/