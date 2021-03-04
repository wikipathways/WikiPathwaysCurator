# Deleted pathways

This test checks if a pathway [PW] linking to another pathway (with an xref using a WikiPathways [WP] identifier)
still exists, and if the linked PW on WPs has not been deleted.

## Why does this fail show up?

There are several reasons why a linked pathway does not exist anymore:
1. The linked PW is deleted
2. The linked PW could be merged with another pathway. 
3. A typo in the identifier is also possible.

## Curation action

If the test fails, it reports pathway DataNodes that link to deteled pathways. The solution is to
check if there is another pathway replacing the deleted pathway. Sometimes the test provides
this information. Actions:

1. open the flagged pathway
2. find the Pathway node with the WikiPathways identifer pointing to a deleted pathway
3. remove or replace the WikiPathways identifer

