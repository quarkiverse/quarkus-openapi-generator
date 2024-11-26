
<div align="center">

<img src="https://github.com/quarkiverse/.github/blob/main/assets/images/quarkus.svg" width="67" height="70" ><img src="https://github.com/quarkiverse/.github/blob/main/assets/images/plus-sign.svg" height="70" ><img src="https://github.com/quarkiverse/quarkus-openapi-generator/blob/main/docs/modules/ROOT/assets/images/openapi.svg" height="70" >

# Quarkus - OpenAPI Generator
</div>
<br>

<!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section -->
[![All Contributors](https://img.shields.io/badge/all_contributors-41-orange.svg?style=flat-square)](#contributors-)
<!-- ALL-CONTRIBUTORS-BADGE:END -->
[![Build](<https://img.shields.io/github/actions/workflow/status/quarkiverse/quarkus-openapi-generator/build.yml?branch=main&logo=GitHub&style=flat-square>)](https://github.com/quarkiverse/quarkus-openapi-generator/actions?query=workflow%3ABuild)
[![Maven Central](https://img.shields.io/maven-central/v/io.quarkiverse.openapi.generator/quarkus-openapi-generator.svg?label=Maven%20Central&style=flat-square)](https://search.maven.org/artifact/io.quarkiverse.openapi.generator/quarkus-openapi-generator)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg?style=flat-square)](https://opensource.org/licenses/Apache-2.0)


> **⚠️** This is the instructions for the latest SNAPSHOT version (main branch). Please, see the [latest **released** documentation](https://docs.quarkiverse.io/quarkus-openapi-generator/dev/index.html) if you are looking for instructions.

> **⚠️** This extension, [like Quarkus 3.7](https://quarkus.io/blog/java-17/), requires Java 17. The last version of this extension that supports earlier versions of Java is [2.2.16](https://github.com/quarkiverse/quarkus-openapi-generator/releases/tag/2.2.16).

> **⚠️** Check versions 1.x.x if you're still using Quarkus 2. But be aware that we no longer support Quarkus 2. That means there are no updates planned for those versions.

Quarkus' extensions for generation of [Rest Clients](https://quarkus.io/guides/rest-client) and server stubs generation based on the [Apicurio Codegen](https://github.com/Apicurio/apicurio-codegen) capabilities based on OpenAPI specification files.

This client-side extension is based on the [OpenAPI Generator Tool](https://openapi-generator.tech/). Please consider donation to help them maintain the
project: https://opencollective.com/openapi_generator/donate

This repository holds two Quarkus extensions. The one located on the client folder is for REST code generation for client side only. The extension located in the server folder can be used for server stubs generation.

**Want to contribute? Great!** We try to make it easy, and all contributions, even the smaller ones, are more than welcome. This includes bug reports, fixes, documentation, examples... But first, read [this page](CONTRIBUTING.md).

## Getting Started

You can learn more in [Quarkus Openapi Generator Documentation](http://docs.quarkiverse.io/quarkus-openapi-generator/dev/index.html).


> If you want to improve the docs, please feel free to contribute editing the docs in [Docs](https://github.com/quarkiverse/quarkus-openapi-generator/tree/main/docs/modules/ROOT). But first, read [this page](CONTRIBUTING.md).

## Contributors ✨

Thanks goes to these wonderful people ([emoji key](https://allcontributors.org/docs/en/emoji-key)):

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tbody>
    <tr>
      <td align="center" valign="top" width="14.28%"><a href="https://ricardozanini.medium.com/"><img src="https://avatars.githubusercontent.com/u/1538000?v=4?s=100" width="100px;" alt="Ricardo Zanini"/><br /><sub><b>Ricardo Zanini</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=ricardozanini" title="Code">💻</a> <a href="#maintenance-ricardozanini" title="Maintenance">🚧</a></td>
      <td align="center" valign="top" width="14.28%"><a href="http://thegreatapi.com"><img src="https://avatars.githubusercontent.com/u/11776454?v=4?s=100" width="100px;" alt="Helber Belmiro"/><br /><sub><b>Helber Belmiro</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=hbelmiro" title="Documentation">📖</a> <a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=hbelmiro" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="http://gastaldi.wordpress.com"><img src="https://avatars.githubusercontent.com/u/54133?v=4?s=100" width="100px;" alt="George Gastaldi"/><br /><sub><b>George Gastaldi</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=gastaldi" title="Code">💻</a> <a href="#infra-gastaldi" title="Infrastructure (Hosting, Build-Tools, etc)">🚇</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/RishiKumarRay"><img src="https://avatars.githubusercontent.com/u/87641376?v=4?s=100" width="100px;" alt="Rishi Kumar Ray"/><br /><sub><b>Rishi Kumar Ray</b></sub></a><br /><a href="#infra-RishiKumarRay" title="Infrastructure (Hosting, Build-Tools, etc)">🚇</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/fjtirado"><img src="https://avatars.githubusercontent.com/u/65240126?v=4?s=100" width="100px;" alt="Francisco Javier Tirado Sarti"/><br /><sub><b>Francisco Javier Tirado Sarti</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=fjtirado" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/Orbifoldt"><img src="https://avatars.githubusercontent.com/u/30009459?v=4?s=100" width="100px;" alt="Orbifoldt"/><br /><sub><b>Orbifoldt</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=Orbifoldt" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/antssilva96"><img src="https://avatars.githubusercontent.com/u/84567479?v=4?s=100" width="100px;" alt="antssilva96"/><br /><sub><b>antssilva96</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=antssilva96" title="Code">💻</a></td>
    </tr>
    <tr>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/wmedvede"><img src="https://avatars.githubusercontent.com/u/2431454?v=4?s=100" width="100px;" alt="Walter Medvedeo"/><br /><sub><b>Walter Medvedeo</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=wmedvede" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/miguelchico"><img src="https://avatars.githubusercontent.com/u/6106661?v=4?s=100" width="100px;" alt="Miguel Angel Chico"/><br /><sub><b>Miguel Angel Chico</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=miguelchico" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/martinweiler"><img src="https://avatars.githubusercontent.com/u/619410?v=4?s=100" width="100px;" alt="Martin Weiler"/><br /><sub><b>Martin Weiler</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=martinweiler" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://leibnizhu.github.io/"><img src="https://avatars.githubusercontent.com/u/13050963?v=4?s=100" width="100px;" alt="Leibniz.Hu"/><br /><sub><b>Leibniz.Hu</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=Leibnizhu" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="http://melloware.com"><img src="https://avatars.githubusercontent.com/u/4399574?v=4?s=100" width="100px;" alt="Melloware"/><br /><sub><b>Melloware</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=melloware" title="Documentation">📖</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/cristianonicolai"><img src="https://avatars.githubusercontent.com/u/570894?v=4?s=100" width="100px;" alt="Cristiano Nicolai"/><br /><sub><b>Cristiano Nicolai</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=cristianonicolai" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/YassinHajaj"><img src="https://avatars.githubusercontent.com/u/18174180?v=4?s=100" width="100px;" alt="YassinHajaj"/><br /><sub><b>YassinHajaj</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=YassinHajaj" title="Code">💻</a></td>
    </tr>
    <tr>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/gwydionmv"><img src="https://avatars.githubusercontent.com/u/118427625?v=4?s=100" width="100px;" alt="Gwydion Martín"/><br /><sub><b>Gwydion Martín</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=gwydionmv" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://www.linkedin.com/in/adrianotagliaferro/"><img src="https://avatars.githubusercontent.com/u/1286247?v=4?s=100" width="100px;" alt="Adriano Augusto Tagliaferro"/><br /><sub><b>Adriano Augusto Tagliaferro</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=dritoferro" title="Tests">⚠️</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://techspace.de"><img src="https://avatars.githubusercontent.com/u/3606282?v=4?s=100" width="100px;" alt="Christian Berger"/><br /><sub><b>Christian Berger</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=chberger" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/brunobaiano"><img src="https://avatars.githubusercontent.com/u/13356327?v=4?s=100" width="100px;" alt="Bruno Alves"/><br /><sub><b>Bruno Alves</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=brunobaiano" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/mcruzdev"><img src="https://avatars.githubusercontent.com/u/56329339?v=4?s=100" width="100px;" alt="Matheus Cruz"/><br /><sub><b>Matheus Cruz</b></sub></a><br /><a href="#infra-mcruzdev" title="Infrastructure (Hosting, Build-Tools, etc)">🚇</a> <a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=mcruzdev" title="Code">💻</a> <a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=mcruzdev" title="Documentation">📖</a></td>
      <td align="center" valign="top" width="14.28%"><a href="http://laurentperez.fr"><img src="https://avatars.githubusercontent.com/u/1085201?v=4?s=100" width="100px;" alt="Laurent Perez"/><br /><sub><b>Laurent Perez</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=laurentperez" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/bpasson"><img src="https://avatars.githubusercontent.com/u/6814512?v=4?s=100" width="100px;" alt="Bas Passon"/><br /><sub><b>Bas Passon</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=bpasson" title="Code">💻</a></td>
    </tr>
    <tr>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/michalsomora"><img src="https://avatars.githubusercontent.com/u/10022003?v=4?s=100" width="100px;" alt="Michal Somora"/><br /><sub><b>Michal Somora</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=michalsomora" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/lordvlad"><img src="https://avatars.githubusercontent.com/u/1217769?v=4?s=100" width="100px;" alt="Waldemar Reusch"/><br /><sub><b>Waldemar Reusch</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=lordvlad" title="Code">💻</a> <a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=lordvlad" title="Documentation">📖</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/patr1kt0th"><img src="https://avatars.githubusercontent.com/u/20856829?v=4?s=100" width="100px;" alt="Patrik Toth"/><br /><sub><b>Patrik Toth</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=patr1kt0th" title="Tests">⚠️</a> <a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=patr1kt0th" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/aecc"><img src="https://avatars.githubusercontent.com/u/6069300?v=4?s=100" width="100px;" alt="Alessandro Chacón"/><br /><sub><b>Alessandro Chacón</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=aecc" title="Tests">⚠️</a> <a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=aecc" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/ninckblokje"><img src="https://avatars.githubusercontent.com/u/2307375?v=4?s=100" width="100px;" alt="ninckblokje"/><br /><sub><b>ninckblokje</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=ninckblokje" title="Code">💻</a> <a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=ninckblokje" title="Tests">⚠️</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/DennisGyldendahlJensenSparNord"><img src="https://avatars.githubusercontent.com/u/135703683?v=4?s=100" width="100px;" alt="DennisGyldendahlJensenSparNord"/><br /><sub><b>DennisGyldendahlJensenSparNord</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=DennisGyldendahlJensenSparNord" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/luanbrdev"><img src="https://avatars.githubusercontent.com/u/144866036?v=4?s=100" width="100px;" alt="Luan Ramalho"/><br /><sub><b>Luan Ramalho</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=luanbrdev" title="Documentation">📖</a></td>
    </tr>
    <tr>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/gabriel-farache"><img src="https://avatars.githubusercontent.com/u/3036508?v=4?s=100" width="100px;" alt="gabriel-farache"/><br /><sub><b>gabriel-farache</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=gabriel-farache" title="Code">💻</a> <a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=gabriel-farache" title="Tests">⚠️</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/martinoneutrino"><img src="https://avatars.githubusercontent.com/u/8833492?v=4?s=100" width="100px;" alt="Martin"/><br /><sub><b>Martin</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=martinoneutrino" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://www.linkedin.com/in/matthias-ngeo/"><img src="https://avatars.githubusercontent.com/u/9427324?v=4?s=100" width="100px;" alt="Matthias Ngeo"/><br /><sub><b>Matthias Ngeo</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=Pante" title="Documentation">📖</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/nmirasch"><img src="https://avatars.githubusercontent.com/u/2443754?v=4?s=100" width="100px;" alt="nmirasch"/><br /><sub><b>nmirasch</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=nmirasch" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://ru4ert.com/"><img src="https://avatars.githubusercontent.com/u/47078678?v=4?s=100" width="100px;" alt="Rupert Bogensperger"/><br /><sub><b>Rupert Bogensperger</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=ru4ert" title="Code">💻</a> <a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=ru4ert" title="Tests">⚠️</a> <a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=ru4ert" title="Documentation">📖</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/boyi01"><img src="https://avatars.githubusercontent.com/u/14997968?v=4?s=100" width="100px;" alt="boyi01"/><br /><sub><b>boyi01</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=boyi01" title="Code">💻</a> <a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=boyi01" title="Tests">⚠️</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/carlesarnal"><img src="https://avatars.githubusercontent.com/u/12640103?v=4?s=100" width="100px;" alt="Carles Arnal"/><br /><sub><b>Carles Arnal</b></sub></a><br /><a href="#maintenance-carlesarnal" title="Maintenance">🚧</a></td>
    </tr>
    <tr>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/brice-laurencin"><img src="https://avatars.githubusercontent.com/u/135709187?v=4?s=100" width="100px;" alt="Brice Laurencin"/><br /><sub><b>Brice Laurencin</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=brice-laurencin" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/gmalheiro"><img src="https://avatars.githubusercontent.com/u/92603922?v=4?s=100" width="100px;" alt="Gabriel Malheiro"/><br /><sub><b>Gabriel Malheiro</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=gmalheiro" title="Documentation">📖</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/kfebert"><img src="https://avatars.githubusercontent.com/u/848796?v=4?s=100" width="100px;" alt="Karl Ferdinand Ebert"/><br /><sub><b>Karl Ferdinand Ebert</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=kfebert" title="Code">💻</a> <a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=kfebert" title="Documentation">📖</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/michalkolenda"><img src="https://avatars.githubusercontent.com/u/29705783?v=4?s=100" width="100px;" alt="Michał Kolenda"/><br /><sub><b>Michał Kolenda</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=michalkolenda" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/rednalyn"><img src="https://avatars.githubusercontent.com/u/31593992?v=4?s=100" width="100px;" alt="rednalyn"/><br /><sub><b>rednalyn</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=rednalyn" title="Documentation">📖</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/marko-bekhta"><img src="https://avatars.githubusercontent.com/u/4004823?v=4?s=100" width="100px;" alt="Marko Bekhta"/><br /><sub><b>Marko Bekhta</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=marko-bekhta" title="Code">💻</a></td>
    </tr>
  </tbody>
</table>

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification. Contributions of any kind welcome!
