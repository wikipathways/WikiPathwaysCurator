# All metabolites are involved in interactions

This test fails for every metabolite that is not involved in at least
one interaction (see doi:[10.1371/journal.pone.0263057](https://doi.org/10.1371/journal.pone.0263057)).
This interaction can be, for example, a metabolic conversion or a binding to a receptor.

The connectivity helps network analysis approaches that use WikiPathways (see
for example doi:[10.1039/D3DD00069A](https://doi.org/10.1039/D3DD00069A)).

## Why does this fail show up?

Basically, when there is no interaction which involves the metabolite, it will
be highlighted. Possible reasons are that it is not linked at all, linked via a graphical
line, or the only other participant in the interaction does not have a
recognized identifier.

## Curation action

Connect the metabolite DataNode with an Interaction.
