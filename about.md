---
layout: page
title: About
permalink: /about/
---

**RightField** is an open-source tool for adding ontology term selection to Excel spreadsheets. RightField is used by a 'Template Creator' to create semantically aware Excel spreadsheet templates. The Excel templates are then reused by Scientists to collect and annotate their data; without any need to understand, or even be aware of, RightField or the ontologies used.

<iframe width="560" height="315" src="https://www.youtube.com/embed/MnuZJ9_IFUI" frameborder="0" allowfullscreen></iframe>

For each annotation field, RightField can specify a range of allowed terms from a chosen ontology (subclasses, individuals or combinations). The resulting spreadsheet presents these terms to the users as a simple drop-down list. This reduces the adoption barrier for using community ontologies as the annotation is made by the scientist that generated the data rather than a third party, and the annotation is collected at the time of data collection.

RightField is a standalone Java application which uses Apache-POI for interacting with Microsoft documents. It enables users to import Excel spreadsheets, or generate new ones from scratch. Ontologies can either be imported from their local file systems, or from the BioPortal ontology repository. Individual cells, or whole columns or rows can be marked with the required ranges of ontology terms and an individual spreadsheet can be annotated with terms from multiple ontologies.

Once marked-up and saved, the RightField-enabled spreadsheet contains 'hidden' sheets with information concerning the origins and versions of ontologies used in the annotation. This provenance information is important in the event of future ontology changes, which may deprecate values already chosen, or may add more fine-grained options and prompt re-annotation.

For the scientist, the main advantages of RightField are that it enables them to consistently annotate their data without the need to explore and understand the numerous standards and ontologies available to them, and it does not require them to change normal practice. Everything is embedded in the Excel spreadsheet.
To find out more about using RightField, please see the User Guide and see Examples of RightField-enabled spreadsheet templates.

RightField is intended as an administrator's tool. It augments spreadsheets that may already conform to specific templates to further standardise terminology. In SysMO-DB spreadsheets are prepared to conform to the "Just Enough Results Model" (JERM), the SysMO-DB internal structure that describes what type of experiment was performed, who performed it, and what was measured. For experiment types with an established minimum information model, the JERM also complies with this. By combining JERM templates and embedded ontology terms with RightField, we provide an infrastructure that promotes and encourages compliance and standardisation. The result is a corpus of data files with consistent annotation that is consequently easier to search and compare.

However, RightField is not tightly coupled to the SysMO-DB JERM infrastructure and can be readily exploited in other applications.

To find out more about using RightField, please see the RightField [User Guide](http:/guide/) and see our [Examples](http:/templates) of RightField-enabled spreadsheet templates.



