# HybridJudge - A Hybrid Judge System

HybridJudge would be a program challenge solution checker, which is of
the kind of programs well known as "judge systems". HybridJudge is
different than the present such systems in the way where HybridJudge
separate the compilation of source code of submitted solution and
execution and evaluation into responsibilities of different parties,
and use JavaScript as the coupling language.

( the following is the project proposal )

## Project Proposal

### Introduction

I propose a project to produce a hybrid server-client model source
code evaluating program that judge source code written as solutions
for specific programming challenges in mathematics or informatics by
compiling, executing the program fed with predefined input, and
checking the output with predefined dataset. This kind of automatic
programming solution evaluators is well known as judge system in the
context of informatics contest, and this project proposal hereby
presents to propose the production of a new kind of such judge system,
which compiles the source code on the server side with the LLVM tool
chain, elaborating emscripten, to generate JavaScript code targeting
asm.js which is then executed and evaluated on the client side, which
is possibly a modern web browser. And the code name for this project
is "HybridJudge", reflecting the nature of the judge system as the
potential resulting product of this project.

### Rationale

Such a new kind of hybrid judge system surpasses the existing systems
from the following aspects:

- As elaborating LLVM as the compiling tool chain, multiple "native
  languages" such as C, C++, Pascal, and other language could be
  supported at the same time, and adding supports to additional
  languages with an LLVM front-end available would be relatively easy.

- By moving the responsibility of execution and evaluation to the
  client, the server could save a significant amount of resources

- Also by moving the responsibility of execution and evaluation to the
  client, the server is not executing any arbitrary codes submitted by
  the clients, which is much more secure from the view of the server.
  The overhead to sandbox the environment that submitted code
  executing in would also be eliminated.

- Although the system is divided into server and client, and the
  server agent usually does not execute or evaluate the submitted
  programs, it is easy to configure the server to also do the
  execution and evaluation when needed, such as in a test or contest
  setup.

### Technologies to be used

- LLVM, as the compilation tool chain
- Emscripten, as the LLVM backend
- Jetty, as the service gateway
- Docker, as the deployment management system
- Java, as the main server side programming language
- JavaScript, as the client side programming language
- Gradle, as the project management and building system
- Git, as the source code management system

### Roadmap

1. Create a docker image that, when deployed, listen at tcp port 80 as
   an HTTP server, which accepts source code and returns compiled
   JavaScript code

1. Create a static html page containing a set of predefined
   input/output so that a user could open the page with a modern
   browser, paste his/her program code in, and let the page request
   the compiled code in JavaScript from the server, execute and
   evaluate the program, and present the result to the user. The page
   should show the title, the task description, the expecting input
   and output format, and a small set of input/output sample of the
   programming challenge.

1. Convert such a static page to a dynamic generated one, so that the
   server could serve the page based on challenge entries stored on a
   external storage, such as a database. This will make content
   management easier, and the service to scale better.

1. Create a system to provide user profile management system, so that
   users and potentially instructors/supervisors could track their
   attempts for long term feedback and analysis and the service could
   be configured to accept only authorized access.

1. Package the docker container so that it could be easily deployed
   locally on an end user's machine, so one won't need to rely on a
   remotely hosted service.

### Potential applications

* As a teaching assisting system for courses on "native" programming
  languages such as C/C++, Pascal, Fortran etc.

* Judge system for contests in informatics and tests in programming
  languages, as well as practice system for those.

* Well crafted self-learning materials on programming languages could
  utilize this system as an exercise checker to assist the learners.

