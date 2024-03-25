import React from 'react';
import { render, wait } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';

import 'components/__mocks__/i18n';
import { apiTkGet } from 'api/tk';
import tkApiGetResponseMock from 'components/__mocks__/tkMocks';
import TkDetailsContainer from 'components/TkDetailsContainer';

jest.mock('api/tk');

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

beforeEach(() => {
  apiTkGet.mockClear();
});

describe('TkDetailsContainer component', () => {
  test('requests data when component is mounted', async () => {
    apiTkGet.mockImplementation(() => Promise.resolve(tkApiGetResponseMock));

    render(<TkDetailsContainer id="1" />);

    await wait(() => {
      expect(apiTkGet).toHaveBeenCalledTimes(1);
    });
  });

  test('data is shown after mount API call', async () => {
    apiTkGet.mockImplementation(() => Promise.resolve(tkApiGetResponseMock));

    const { getByText } = render(<TkDetailsContainer id="1" />);

    await wait(() => {
      expect(apiTkGet).toHaveBeenCalledTimes(1);
      expect(getByText('entities.tk.id')).toBeInTheDocument();
      expect(getByText('entities.tk.field1')).toBeInTheDocument();
      expect(getByText('entities.tk.field2')).toBeInTheDocument();
    });
  });

  test('error is shown after failed API call', async () => {
    const onErrorMock = jest.fn();
    apiTkGet.mockImplementation(() => Promise.reject());

    const { getByText } = render(<TkDetailsContainer id="1" onError={onErrorMock} />);

    await wait(() => {
      expect(apiTkGet).toHaveBeenCalledTimes(1);
      expect(onErrorMock).toHaveBeenCalledTimes(1);
      expect(getByText('error.dataLoading')).toBeInTheDocument();
    });
  });
});
