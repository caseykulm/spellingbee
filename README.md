# Spelling Bee

NYT Spelling Bee board generating tool.

## Dictionary

I've used Matthew Reagan's [Webster Dictionary](https://github.com/matthewreagan/WebstersEnglishDictionary) repository.

## Rules of Spelling Bee Boards

Spelling bee boards must...

* contain 7 unique characters.
* designate a single character as the center character.
* place all other characters elsewhere on the board.

Spelling bee boards may...

* place other characters anywhere other than the center character.

## Rules of Spelling Bee Answers

Spelling bee answers must...

* include the center character
* be 4 characters or longer

Spelling bee answers may...

* contain the same letter multiple times. 

## Custom Data Structures

### UniqueCharSet

Given some input word, it will map it to a data structure that contains relevant information about it's unique 
characters. It also implements equality/hashcode based on the unique characters. 

e.g. 

```kotlin
// These two instances would be considered equal
UniqueCharSet("foo") == UniqueCharSet("oof")

// Duplicate characters are handled as well
UniqueCharSet("foo") == UniqueCharSet("of")
```
