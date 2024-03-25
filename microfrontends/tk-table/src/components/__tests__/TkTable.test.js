import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import { fireEvent, render } from '@testing-library/react';
import i18n from 'components/__mocks__/i18n';
import tkMocks from 'components/__mocks__/tkMocks';
import TkTable from 'components/TkTable';

describe('TkTable', () => {
  it('shows tks', () => {
    const { getByText } = render(<TkTable items={tkMocks} />);

    expect(getByText(tkMocks[0].id.toString())).toBeInTheDocument();
    expect(getByText(tkMocks[1].id.toString())).toBeInTheDocument();

    expect(getByText(tkMocks[0].field1)).toBeInTheDocument();
    expect(getByText(tkMocks[1].field1)).toBeInTheDocument();

    expect(getByText(tkMocks[0].field2.toString())).toBeInTheDocument();
    expect(getByText(tkMocks[1].field2.toString())).toBeInTheDocument();
  });

  it('shows no tks message', () => {
    const { queryByText } = render(<TkTable items={[]} />);

    expect(queryByText(tkMocks[0].id.toString())).not.toBeInTheDocument();
    expect(queryByText(tkMocks[1].id.toString())).not.toBeInTheDocument();

    expect(queryByText(tkMocks[0].field1)).not.toBeInTheDocument();
    expect(queryByText(tkMocks[1].field1)).not.toBeInTheDocument();

    expect(queryByText(tkMocks[0].field2.toString())).not.toBeInTheDocument();
    expect(queryByText(tkMocks[1].field2.toString())).not.toBeInTheDocument();

    expect(queryByText('entities.tk.noItems')).toBeInTheDocument();
  });

  it('calls onSelect when the user clicks a table row', () => {
    const onSelectMock = jest.fn();
    const { getByText } = render(<TkTable items={tkMocks} onSelect={onSelectMock} />);

    fireEvent.click(getByText(tkMocks[0].id.toString()));
    expect(onSelectMock).toHaveBeenCalledTimes(1);
  });
});
