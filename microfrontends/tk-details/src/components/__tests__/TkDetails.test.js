import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import { render } from '@testing-library/react';

import 'components/__mocks__/i18n';
import TkDetails from 'components/TkDetails';
import tkMock from 'components/__mocks__/tkMocks';

describe('TkDetails component', () => {
  test('renders data in details widget', () => {
    const { getByText } = render(<TkDetails tk={tkMock} />);

    expect(getByText('entities.tk.id')).toBeInTheDocument();
  });
});
