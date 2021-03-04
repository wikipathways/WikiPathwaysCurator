# Deleted pathways

This test checks if a pathway links to another pathway with an xref using a WikiPathways identifier
still exists and has not been deleted.

## Why does this fail show up?

The reason for a linked pathway is deleted could be that the pathway was merged with another
pathway. Of course, a typo in the identifier is also possible.

## Curation action

If the test fails, it reports pathways that link to deteled pathways. The solution is to
check if there is another pathway replacing the deleted pathway. Sometimes the test provides
this information. Actions:

1. open the flagged pathway
2. find the Pathway node with the WikiPathways identifer pointing to a deleted pathway
3. remove or replace the WikiPathways identifer

