<div align="center" style="display: flex; flex-direction: column;">
  <img src="./public/logo-white_bg.png" alt="sequencer logo" width="500px" />
  <h3>sequencer is a simple diagramming tool to make it easy for developers to share ideas</h3>
  <p>
      <img src="https://github.com/rsouth/sequencer/workflows/Java%20CI%20with%20Maven/badge.svg?branch=develop">
      <img src="https://github.com/rsouth/sequencer/workflows/Maven%20Package/badge.svg">
      <a href="https://codeclimate.com/github/rsouth/sequencer/maintainability"><img src="https://api.codeclimate.com/v1/badges/7cc6ac9e91e80b4cdbbc/maintainability" /></a>
  </p>
</div>

## What is sequencer?
Sequencer allows you to create **simple and clear sequence diagrams**, based on a simple grammar. Your diagram is **drawn in real-time** as you type, and is designed to be:
 - easy to **edit**
 - easy to **share**
 - easy to **version control**

## Simple to get started

#### Describe your sequence
`Client -> Server: Request`

`Server -> Server: Parses request`

`Server -> Service: Query`

`Service -> Server: Data`

`Server -> Client: Response`

#### Your diagram is ready to go

<img src="https://i.ibb.co/FDT8kNL/sequencer-example-2020-08-07.png" alt="sequencer-example-2020-08-07" border="0" />

Copy the diagram to your clipboard to paste straight in to chat or email.

See the [wiki](https://github.com/rsouth/sequencer/wiki) for the current grammar.

<div align="center" style="display: flex; flex-direction: column;">
  <p>
    <img src="https://s7.gifyu.com/images/sequence-gizmo-6.gif" alt="sequence-gizmo-6.gif" border="0" />
  </p>
</div>

## Contributing
**Pull requests are welcome**. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## Credits / Attributions

#### Inspiration

https://www.websequencediagrams.com/ is a website offering text-based sequence diagrams with a commercial offering.

## License

This project is licensed under the terms of the GNU General Public License v3.0.
