# hc-ov-pizza-verification-plugins

This repository contains a implementation of the `IVerificationTaskPlugin` and `IContextProviderPlugin` plugins
for [hc-ov-core](https://github.com/k-klemens/hc-ov-core) to replicate the human-centred ontology verification of [1] as closely as possible. (for more
information about the interfaces and the plugins see [hc-ov-sdk](https://github.com/k-klemens/hc-ov-sdk))

Following functionality is provided by the plugins:

* **RestrictionVerificationPlugin** (implementation of `IVerificationTaskPlugin`): Extracts the `owl:allValuesFrom` and `owl:someValuesFrom` on the `hasTopping`
  properties of `#NamedPizza` classes and groups them as axioms.
  Further, the plugin provides a HTML template and resolving mechanism of templating variables to verify
  whether the used axioms are
  correct to model the toppings of a pizza. The template supports rendering the restrictions in "Warren"[2] and "Rector"[3] formalism.
* **PizzaMenuContextProviderPlugin** (implementation of `IContextProviderPlugin`): Extracts all toppings specified using the `hasTopping`property of a pizza,
  adds a random pizza image and encodes this contextual information in a string to be used when populating the templates.

For the `RestrictionVerificationPlugin` plugin following configuration keys passed with `verificationTaskPluginConfiguration` in the verification specification
are required:

* _REPRESENTATION_MECHANISM_: either _RECTOR_ or _WARREN_ to specify how to represent the axioms.

[1] S. S. Tsaneva, “Human-Centric Ontology Evaluation,” TU Wien, 2021, doi: 10.34726/HSS.2021.79389.

[2] P. Warren, P. Mulholland, T. Collins, and E. Motta, “Improving comprehension of knowledge representation languages: a case study with description logics,”
International Journal of Human-Computer Studies, vol. 122, pp. 145–167, 2019.

[3] A. Rector et al., “OWL pizzas: Practical experience of teaching OWL-DL: Common errors & common patterns,” in International Conference on Knowledge
Engineering and Knowledge Management, 2004, pp. 63–81.
