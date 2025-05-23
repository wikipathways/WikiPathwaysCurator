# Unconnected Points

The GPML data models allows linking data nodes with interactions, or an interaction with another
interaction. For example, an enzymatic reaction has two interactions; one between two metabolite
nodes, and the second between the enzyme and the first interaction.

An interaction that does not connect two entities properly via the interaction's start and end point,
is called "unconnected". These unconnected points (i.e. ends of interactions), limit how efficiently the pathway can be used
in, for example, network analysis.

## Why does this fail show up?

Ideally, all interactions connect to other entities. The fail signals that this is not the case.

## Curation action

The interaction should be looked at and should not have unconnected points.
In PathVisio 3.3, you can use the `Ctrl-L` functionality to highlight in green the unconnected points.
