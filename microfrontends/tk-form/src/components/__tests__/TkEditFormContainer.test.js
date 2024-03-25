import React from 'react';
import { fireEvent, render, wait } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import { apiTkGet, apiTkPut } from 'api/tks';
import TkEditFormContainer from 'components/TkEditFormContainer';
import 'i18n/__mocks__/i18nMock';
import { tkMockEdit as tkMock } from 'components/__mocks__/tkMocks';

const configMock = {
  systemParams: {
    api: {
      'tk-api': {
        url: '',
      },
    },
  },
};

jest.mock('api/tks');

jest.mock('auth/withKeycloak', () => {
  const withKeycloak = (Component) => {
    return (props) => (
      <Component
        {...props} // eslint-disable-line react/jsx-props-no-spreading
        keycloak={{
          initialized: true,
          authenticated: true,
        }}
      />
    );
  };

  return withKeycloak;
});

describe('TkEditFormContainer', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  const errorMessageKey = 'error.dataLoading';
  const successMessageKey = 'common.dataSaved';

  const onErrorMock = jest.fn();
  const onUpdateMock = jest.fn();

  it('loads data', async () => {
    apiTkGet.mockImplementation(() => Promise.resolve(tkMock));
    const { queryByText } = render(
      <TkEditFormContainer
        id="1"
        onError={onErrorMock}
        onUpdate={onUpdateMock}
        config={configMock}
      />
    );

    await wait(() => {
      expect(apiTkGet).toHaveBeenCalledTimes(1);
      expect(apiTkGet).toHaveBeenCalledWith('', '1');
      expect(queryByText(errorMessageKey)).not.toBeInTheDocument();
      expect(onErrorMock).toHaveBeenCalledTimes(0);
    });
  }, 7000);

  it('saves data', async () => {
    apiTkGet.mockImplementation(() => Promise.resolve(tkMock));
    apiTkPut.mockImplementation(() => Promise.resolve(tkMock));

    const { findByTestId, queryByText } = render(
      <TkEditFormContainer
        id="1"
        onError={onErrorMock}
        onUpdate={onUpdateMock}
        config={configMock}
      />
    );

    const saveButton = await findByTestId('submit-btn');

    fireEvent.click(saveButton);

    await wait(() => {
      expect(apiTkPut).toHaveBeenCalledTimes(1);
      expect(apiTkPut).toHaveBeenCalledWith('', tkMock.id, tkMock);
      expect(queryByText(successMessageKey)).toBeInTheDocument();
      expect(onErrorMock).toHaveBeenCalledTimes(0);
      expect(queryByText(errorMessageKey)).not.toBeInTheDocument();
    });
  }, 7000);

  it('shows an error if data is not successfully loaded', async () => {
    apiTkGet.mockImplementation(() => Promise.reject());
    const { queryByText } = render(
      <TkEditFormContainer
        id="1"
        onError={onErrorMock}
        onUpdate={onUpdateMock}
        config={configMock}
      />
    );

    await wait(() => {
      expect(apiTkGet).toHaveBeenCalledTimes(1);
      expect(apiTkGet).toHaveBeenCalledWith('', '1');
      expect(onErrorMock).toHaveBeenCalledTimes(1);
      expect(queryByText(errorMessageKey)).toBeInTheDocument();
      expect(queryByText(successMessageKey)).not.toBeInTheDocument();
    });
  }, 7000);

  it('shows an error if data is not successfully saved', async () => {
    apiTkGet.mockImplementation(() => Promise.resolve(tkMock));
    apiTkPut.mockImplementation(() => Promise.reject());
    const { findByTestId, getByText } = render(
      <TkEditFormContainer id="1" onError={onErrorMock} config={configMock} />
    );

    const saveButton = await findByTestId('submit-btn');

    fireEvent.click(saveButton);

    await wait(() => {
      expect(apiTkGet).toHaveBeenCalledTimes(1);
      expect(apiTkGet).toHaveBeenCalledWith('', '1');

      expect(apiTkPut).toHaveBeenCalledTimes(1);
      expect(apiTkPut).toHaveBeenCalledWith('', tkMock.id, tkMock);

      expect(onErrorMock).toHaveBeenCalledTimes(1);
      expect(getByText(errorMessageKey)).toBeInTheDocument();
    });
  }, 7000);
});
