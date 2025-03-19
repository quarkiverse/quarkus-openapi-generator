import {LitElement, html, css} from 'lit';
import {columnBodyRenderer} from '@vaadin/grid/lit.js';
import {mocks} from 'build-time-data';
import '@vaadin/grid';
import '@vaadin/vertical-layout';
import '@vaadin/icon';

/**
 * This component shows the Moqu mocks
 */
export class QwcMoqu extends LitElement {

    static styles = css`
        .arctable {
            height: 100%;
            padding-bottom: 10px;
        }

        .moqu-icon {
            font-size: small;
            color: var(--lumo-contrast-50pct);
            cursor: pointer;
        }
    `;

    static properties = {
        _mocks: {state: true}
    };

    constructor() {
        super();
        this._mocks = mocks;
    }

    render() {
        if (this._mocks) {
            return this._renderMockList();
        } else {
            return html`No mocks found`;
        }
    }

    _renderMockList() {
        return html`
            <vaadin-grid .items="${this._mocks}" class="arctable" theme="no-border">
                <vaadin-grid-column auto-width
                                    header="Name"
                                    ${columnBodyRenderer(this._nameRenderer, [])}
                                    resizable>
                </vaadin-grid-column>

                <vaadin-grid-column auto-width
                                    header="Download"
                                    ${columnBodyRenderer(this._linkDownloadRenderer, [])}
                                    resizable>
                </vaadin-grid-column>
                <vaadin-grid-column auto-width
                                    header="See"
                                    ${columnBodyRenderer(this._linkSeeRenderer, [])}
                                    resizable>
                </vaadin-grid-column>
            </vaadin-grid>`;
    }

    _nameRenderer(mock) {
        return html`
            <vaadin-vertical-layout>
                ${mock.name}
            </vaadin-vertical-layout>
        `;
    }

    _linkDownloadRenderer(mock) {
        return html`
            <vaadin-vertical-layout>
                <a href="${mock.link}" target="_blank">
                    <vaadin-icon class="moqu-icon" icon="font-awesome-solid:download"></vaadin-icon>
                </a>
            </vaadin-vertical-layout>
        `;
    }

    _linkSeeRenderer(mock) {
        return html`
            <vaadin-vertical-layout>
                <a href="${mock.link}?mode=see" target="_blank">
                    <vaadin-icon class="moqu-icon" icon="font-awesome-solid:eye"></vaadin-icon>
                </a>
            </vaadin-vertical-layout>
        `;
    }


}

customElements.define('qwc-moqu', QwcMoqu);
