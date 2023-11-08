import Button from 'components/atoms/Button';
import { SwitchButtonContainer } from './style';

interface ISwitchButtonProps {
  selectedButton: number;
  setSelectedButton: (value: number) => void;
  firstButton: string;
  secondButton: string;
  disabled: boolean; // 새로운 disabled 프로퍼티 추가
}

function SwitchButton({ selectedButton, setSelectedButton, firstButton, secondButton, disabled }: ISwitchButtonProps) {
  const handleClick = (buttonNumber: number) => {
    setSelectedButton(buttonNumber);
  };

  return (
    <SwitchButtonContainer>
      <Button
        size="m"
        radius="l"
        color={selectedButton === 1 ? 'Primary' : 'SubFirst'}
        text={firstButton}
        handleClick={() => handleClick(1)}
      />
      <Button
        size="m"
        radius="l"
        color={selectedButton === 2 ? 'Primary' : 'SubFirst'}
        text={secondButton}
        handleClick={() => handleClick(2)}
        disabled={disabled} // disabled 상태에 따라 버튼 활성화 여부 조절
      />
    </SwitchButtonContainer>
  );
}

export default SwitchButton;