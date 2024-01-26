# (Simple-ish) Scala Http4s API

The Intention of this project was to construct a working example of a functional style API uisng the following tools:

1. Api/Server -> Http4s, a functional http server / http request library - this is the core of our application
2. Database -> Doobie, a functional db layer, essentially a functional wrapper over jdbc. This allows for type-safe fetching of data from DB.
3. Configuration -> PureConfig, functional configuration tool
4. DB Migrations -> Flyway is a DB migrations tool that fits nicely within our ecosystem
5. JSON decoder/encoder -> Circe, we use this to define mappings between DB entities and our case classes in our model. Using Circe Generic Auto is even more hands off, allowing for inferred type-safe conversion.
6. Cats + Cats-effect -> We use both of these for the type level definitions and implicits they provide. 
    => Cats is for the abstractions we commonly use in functional programming (Functors, Monads, Applicatives...)
    
    => Cats-effect is used for the IO Monad and to make composable, high performance async applications. Using this we can handle side effects very very specifically.

## A bit about the Typelevel ecosystem
This stack is often called the Typelevel ecosystem and describes a style of programming which focuses not only on specific implementations, but on the relationships between Types as well. Often this leads to more concise code, with a particular emphasis on single responsibility principles.

### Advantages:
- Concise, as we repeat ourselves much less in this style of programming, we do not need to redefine common behaviours over and over. Just use a single implementation and compose it into the function you need.
- Composable, we write code that allows us to combine functionality together, this often leads to the ability to abstract away complexity and hide it from the end user of the code, whilst still providing all the functionality.
- Testable, as we break our functionality down into many small functions, it is very easy to test specific parts of our app without mocking huge sections of it. Automates suites work wonders with this stack.
- Cats-effect specifically provides a solution to 'the async problem' - both Future and Task (used for thread handling in async prog) have issues with their implementations. The cats effect datatypes provided, attempt to categorise different behaviours for async computation, and allow us - using a tagless final (F[_] : ...) to have fine grain control over how and when our side-effects are actually executed!

### Disadvantages:
- Its complicated, In order to abstract complexity away from the exposed interface we have to be able to talk about things in _general_ terms. This can often be a lot to hold in ones head!
- The barrier to entry is high, to properly use this tech stack, you need a pretty extensive theoretical understanding of the underlying concepts! Whilst a full understanding of category theory is not required to start using these libraries practically, many of the concepts are used here.
- Expensive - this stuff is hard, which means we need intelligent people to work on it, which means it can be expensive!

## Is it necessary here?
- The short answer to this is no! We have neither a requirement for fierce performance, nor for Async computation. 
- As demonstrated in this project: If you are only contraining your F[_] types to Async, you are missing the point! In this project the vast majority of contraints are F[_]: Async. Which shows we are not utilising cats-effect or the tagless final to its full extent and point to a simpler implementation for a production API.
- However, this is a good working repo to test out and apply different ideas to the typelevel ecosystem (as a beginner!)

