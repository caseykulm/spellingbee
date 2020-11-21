# Spelling Bee

NYT Spelling Bee board generating tool.

## Dictionary

### Matthew Reagan's Webster Dictionary

[Source](https://github.com/matthewreagan/WebstersEnglishDictionary)

This dictionary seems to be missing a lot of words, but also has a ton of uncommon words.

### Google's 10k Most Used English n-grams

[Source](https://github.com/first20hours/google-10000-english)

Specifically using the [10k most common words](https://raw.githubusercontent.com/first20hours/google-10000-english/master/google-10000-english.txt).

### Peter Norvig's n-grams

[Source](https://norvig.com/ngrams/)

Specifically using the [1/3 Million most common words](https://norvig.com/ngrams/count_1w.txt), but using `sed 's/[0-9]*//g'` to 
remove the frequencies.

This does not contain all the missing words I've been looking for, and has a lot of words that clearly don't exist.

### Dwyl >466k English Words

[Source](https://github.com/dwyl/english-words)

Contains a lot of the missing words I've been looking for, but also has a lot of words that clearly don't exist. 

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
