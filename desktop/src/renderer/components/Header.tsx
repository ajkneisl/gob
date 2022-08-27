import { Heading } from 'grommet';
import styled from 'styled-components';

const Header = styled.header`
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: center;
    padding: 16px;
`;

export default () => (
    <Header>
        <Heading level="1">
            gobspeak — <a href="#">home</a>, <a href="#">friends</a>,{' '}
            <a href="#">messaging</a>
        </Heading>
    </Header>
);
