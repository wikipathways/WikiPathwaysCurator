# Interactions with Labels

Unlike `DataNode`s `Label`s in WikiPathways GPML pathways do not have semantic knowledge, because of the lack
of a linked identifier. `Interaction`s are used to link two `DataNode`s. Labels should be linked with
graphical lines.

## Why does this fail show up?

The Interaction links to a Label.

## Curation action

There are two possible solutions to this fail. First, you can convert the `Label` to which the `Interaction`
links into a `DataNode`. Second, you can convert the `Interaction` into a graphical line.
