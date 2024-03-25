import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import { fireEvent, render, wait } from '@testing-library/react';
import i18n from 'i18n/__mocks__/i18nMock';
import { tkMockEdit as tkMock } from 'components/__mocks__/tkMocks';
import TkForm from 'components/TkForm';
import { createMuiTheme } from '@material-ui/core';
import { ThemeProvider } from '@material-ui/styles';

const theme = createMuiTheme();

describe('Tk Form', () => {
  it('shows form', () => {
    const { getByLabelText, getByTestId } = render(
      <ThemeProvider theme={theme}>
        <TkForm tk={tkMock} />
      </ThemeProvider>
    );

    expect(getByTestId('tk-id').value).toBe(tkMock.id.toString());
    expect(getByLabelText('entities.tk.field1').value).toBe(tkMock.field1);
    expect(getByLabelText('entities.tk.field2').value).toBe(tkMock.field2.toString());
  });

  it('submits form', async () => {
    const handleSubmit = jest.fn();
    const { getByTestId } = render(
      <ThemeProvider theme={theme}>
        <TkForm tk={tkMock} onSubmit={handleSubmit} />
      </ThemeProvider>
    );

    const form = getByTestId('tk-form');
    fireEvent.submit(form);

    await wait(() => {
      expect(handleSubmit).toHaveBeenCalledTimes(1);
    });
  });
});
