import React from 'react';
import { fireEvent, render, wait } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import { apiTkPost } from 'api/tks';
import TkAddFormContainer from 'components/TkAddFormContainer';
import 'i18n/__mocks__/i18nMock';
import { tkMockAdd as tkMock } from 'components/__mocks__/tkMocks';

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
jest.mock('@material-ui/pickers', () => {
  // eslint-disable-next-line react/prop-types
  const MockPicker = ({ id, value, name, label, onChange }) => {
    const handleChange = (event) => onChange(event.currentTarget.value);
    return (
      <span>
        <label htmlFor={id}>{label}</label>
        <input id={id} name={name} value={value || ''} onChange={handleChange} />
      </span>
    );
  };
  return {
    ...jest.requireActual('@material-ui/pickers'),
    DateTimePicker: MockPicker,
    DatePicker: MockPicker,
  };
});

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

describe('TkAddFormContainer', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  const errorMessageKey = 'error.dataLoading';
  const successMessageKey = 'common.dataSaved';

  const onErrorMock = jest.fn();
  const onCreateMock = jest.fn();

  it('saves data', async () => {
    apiTkPost.mockImplementation((data) => Promise.resolve(data));

    const { findByTestId, findByLabelText, queryByText, rerender } = render(
      <TkAddFormContainer onError={onErrorMock} onUpdate={onCreateMock} config={configMock} />
    );

    const field1Field = await findByLabelText('entities.tk.field1');
    fireEvent.change(field1Field, { target: { value: tkMock.field1 } });
    const field2Field = await findByLabelText('entities.tk.field2');
    fireEvent.change(field2Field, { target: { value: tkMock.field2 } });
    rerender(
      <TkAddFormContainer onError={onErrorMock} onUpdate={onCreateMock} config={configMock} />
    );

    const saveButton = await findByTestId('submit-btn');

    fireEvent.click(saveButton);

    await wait(() => {
      expect(apiTkPost).toHaveBeenCalledTimes(1);
      expect(apiTkPost).toHaveBeenCalledWith('', tkMock);

      expect(queryByText(successMessageKey)).toBeInTheDocument();

      expect(onErrorMock).toHaveBeenCalledTimes(0);
      expect(queryByText(errorMessageKey)).not.toBeInTheDocument();
    });
  }, 7000);

  it('shows an error if data is not successfully saved', async () => {
    apiTkPost.mockImplementation(() => Promise.reject());

    const { findByTestId, findByLabelText, queryByText, rerender } = render(
      <TkAddFormContainer onError={onErrorMock} onUpdate={onCreateMock} config={configMock} />
    );

    const field1Field = await findByLabelText('entities.tk.field1');
    fireEvent.change(field1Field, { target: { value: tkMock.field1 } });
    const field2Field = await findByLabelText('entities.tk.field2');
    fireEvent.change(field2Field, { target: { value: tkMock.field2 } });
    rerender(
      <TkAddFormContainer onError={onErrorMock} onUpdate={onCreateMock} config={configMock} />
    );

    const saveButton = await findByTestId('submit-btn');

    fireEvent.click(saveButton);

    await wait(() => {
      expect(apiTkPost).toHaveBeenCalledTimes(1);
      expect(apiTkPost).toHaveBeenCalledWith('', tkMock);

      expect(queryByText(successMessageKey)).not.toBeInTheDocument();

      expect(onErrorMock).toHaveBeenCalledTimes(1);
      expect(queryByText(errorMessageKey)).toBeInTheDocument();
    });
  }, 7000);
});
