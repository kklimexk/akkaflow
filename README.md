# **Akkaflow** #

[![Build Status](https://travis-ci.org/kkrzys/akkaflow.svg?branch=master)](https://travis-ci.org/kkrzys/akkaflow)

*Akkaflow* - workflow DSL library for scientific computations based on Akka actors.

This project was created as a part of the master thesis.

*Akkaflow* is written entirely in Scala and using Akka technology.

## Goal ##

The aim of this library is to provide a simple library to program workflows. For this purpose was created a dedicated API - internal DSL to create workflows as simple as possible.

## Running examples: ##

```
$ sbt run
```
 or 
```
$ ./activator run
```

## Getting started ##

Creating simple workflow:


```Scala

val sqr = { (in: Int, outs: Outs) =>
  outs().foreach(out => in * in =>> out)
}

val splitProc = Split[Int, Int] (
  name = "splitProc",
  numOfOuts = 3,
  action = sqr
)

val w = Workflow (
  "Example split workflow",
  numOfIns = 1,
  numOfOuts = 3,
  (ins: Seq[In[Int]], outs: Seq[Out[Int]]) => {
    ins(0) ~>> splitProc
    splitProc.outs(0) ~>> outs(0)
    splitProc.outs(1) ~>> outs(1)
    splitProc.outs(2) ~>> outs(2)
  }
)

Source(1 to 10) ~> w.ins(0)
val res = w.run
println(res)
println(w)
```

All the examples are available in: 

*/src/main/scala/pl/edu/agh/examples/*