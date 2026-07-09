# Modelo de Domínio

> Este documento descreve os principais conceitos do domínio do Projeto Ninho. Ele estabelece uma linguagem comum para a arquitetura, a implementação e a documentação do sistema.

---

# Visão Geral

O Projeto Ninho é uma plataforma de inteligência ambiental centrada nas pessoas. Seu domínio é composto por entidades que representam moradores, ambientes, dispositivos, automações e inteligência artificial, todos colaborando para oferecer uma experiência integrada.

Este documento define os conceitos fundamentais do domínio, independentemente de decisões de implementação.

---

# Conceitos Fundamentais

## Residência

Representa o espaço físico administrado pela plataforma.

Uma residência agrupa ambientes, dispositivos, moradores e configurações compartilhadas.

Exemplos:

* Casa
* Apartamento
* Escritório
* Chácara

---

## Ambiente

Representa uma divisão física dentro de uma residência.

Exemplos:

* Sala
* Cozinha
* Quarto
* Escritório
* Garagem

Um ambiente pode conter diversos dispositivos, sensores e automações.

---

## Morador

Pessoa autorizada a utilizar a plataforma.

Um morador possui identidade, permissões, preferências e pode interagir com a inteligência artificial.

Exemplos:

* Administrador
* Adulto
* Criança

---

## Dispositivo

Qualquer equipamento integrado ao Projeto Ninho.

Pode representar dispositivos físicos ou virtuais.

Exemplos:

* Lâmpada
* Interruptor
* Sensor
* Televisão
* Alto-falante
* Câmera
* Cyberdeck

---

## Sensor

Dispositivo responsável por observar o ambiente.

Sensores produzem eventos, mas não executam ações.

Exemplos:

* Movimento
* Temperatura
* Umidade
* Luminosidade
* Abertura de portas
* Presença

---

## Atuador

Dispositivo capaz de modificar o ambiente.

Exemplos:

* Lâmpada
* Fechadura
* Relé
* Cortina
* Ventilador
* Ar-condicionado

---

## Evento

Representa algo que ocorreu dentro do ecossistema.

Eventos são imutáveis e descrevem fatos.

Exemplos:

* Movimento detectado
* Porta aberta
* Luz acesa
* Comando de voz recebido
* Persona ativada

Eventos podem originar automações ou alimentar a memória da IA.

---

## Automação

Conjunto de regras que responde a eventos do sistema.

Uma automação pode analisar contexto antes de executar ações.

Exemplos:

* Acender luz ao detectar presença.
* Enviar notificação quando uma porta permanecer aberta.
* Silenciar notificações durante a madrugada.

---

## Inteligência Artificial

Núcleo responsável por compreender contexto, interpretar linguagem natural e auxiliar os moradores.

A inteligência artificial pode utilizar diferentes modelos e ferramentas, mas sua função permanece a mesma: oferecer suporte contextual aos usuários.

---

## Persona

Representa uma forma especializada de interação da inteligência artificial.

Cada persona possui comportamento, tom de comunicação, memória e permissões próprias.

Exemplos:

* Assistente da residência
* Assistente infantil
* Assistente técnico
* Visitante

Uma persona não é um modelo de IA, mas uma configuração de comportamento sobre o mesmo núcleo inteligente.

---

## Memória

Conjunto de informações persistentes utilizadas pela inteligência artificial para manter contexto ao longo do tempo.

A memória pode armazenar preferências, histórico de interações, conhecimento operacional e outras informações relevantes.

Ela existe para melhorar a continuidade da experiência, sempre respeitando as configurações de privacidade definidas pelos moradores.

---

## Comunicação

Mecanismo pelo qual moradores, dispositivos e serviços trocam informações.

Pode ocorrer por voz, texto, notificações, eventos ou integrações externas.

---

## Integração

Representa qualquer sistema externo conectado ao Projeto Ninho.

Exemplos:

* Home Assistant
* Telegram
* Discord
* WhatsApp

As integrações ampliam as capacidades da plataforma sem alterar seu núcleo de funcionamento.

---

# Relações entre os conceitos

Em alto nível:

* Uma Residência possui Ambientes.
* Um Ambiente contém Dispositivos.
* Dispositivos podem ser Sensores ou Atuadores.
* Sensores produzem Eventos.
* Eventos podem disparar Automações.
* Automações executam ações sobre Atuadores.
* Moradores interagem com a Inteligência Artificial.
* A Inteligência Artificial utiliza Personas e Memória para fornecer respostas contextualizadas.
* Integrações permitem a comunicação com sistemas externos.

---

# Linguagem Ubíqua

Os termos definidos neste documento devem ser utilizados de forma consistente em toda a documentação, código-fonte, APIs, eventos e banco de dados do Projeto Ninho.

Caso um novo conceito relevante seja identificado, ele deverá ser documentado aqui antes de ser incorporado à arquitetura.
