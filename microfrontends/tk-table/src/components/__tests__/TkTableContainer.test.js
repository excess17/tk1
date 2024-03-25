import React from 'react';
import { render, wait } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';

import tkMocks from 'components/__mocks__/tkMocks';
import { apiTksGet } from 'api/tks';
import 'i18n/__mocks__/i18nMock';
import TkTableContainer from 'components/TkTableContainer';

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

jest.mock('components/pagination/withPagination', () => {
  const withPagination = (Component) => {
    return (props) => (
      <Component
        {...props} // eslint-disable-line react/jsx-props-no-spreading
        pagination={{
          onChangeItemsPerPage: () => {},
          onChangeCurrentPage: () => {},
        }}
      />
    );
  };

  return withPagination;
});

describe('TkTableContainer', () => {
  const errorMessageKey = 'error.dataLoading';

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('calls API', async () => {
    apiTksGet.mockImplementation(() => Promise.resolve({ tks: tkMocks, count: 2 }));
    const { queryByText } = render(<TkTableContainer />);

    await wait(() => {
      expect(apiTksGet).toHaveBeenCalledTimes(1);
      expect(queryByText(errorMessageKey)).not.toBeInTheDocument();
    });
  });

  it('shows an error if the API call is not successful', async () => {
    apiTksGet.mockImplementation(() => {
      throw new Error();
    });
    const { getByText } = render(<TkTableContainer />);

    wait(() => {
      expect(apiTksGet).toHaveBeenCalledTimes(1);
      expect(getByText(errorMessageKey)).toBeInTheDocument();
    });
  });
});
